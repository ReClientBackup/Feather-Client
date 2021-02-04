package com.murengezi.minecraft.crash;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.util.ReportedException;
import net.minecraft.world.gen.layer.IntCache;
import net.optifine.CrashReporter;
import net.optifine.reflect.Reflector;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport {

    private static final Logger logger = LogManager.getLogger();
    private final String description;
    private final Throwable cause;

    private final CrashReportCategory theReportCategory = new CrashReportCategory("System Details");
    private final List<CrashReportCategory> crashReportSections = Lists.newArrayList();

    private File crashReportFile;
    private boolean field_85059_f = true;
    private StackTraceElement[] stacktrace = new StackTraceElement[0];
    private boolean reported = false;

    public CrashReport(String description, Throwable cause) {
        this.description = description;
        this.cause = cause;
        this.populateEnvironment();
    }

    private void populateEnvironment()
    {
        this.theReportCategory.addCrashSectionCallable("Minecraft Version", () -> "1.8.10");
        this.theReportCategory.addCrashSectionCallable("Operating System", () -> System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
        this.theReportCategory.addCrashSectionCallable("Java Version", () -> System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        this.theReportCategory.addCrashSectionCallable("Java VM Version", () -> System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        this.theReportCategory.addCrashSectionCallable("Memory", () -> {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long maxMemoryMB = maxMemory / 1024L / 1024L;
            long totalMemoryMB = totalMemory / 1024L / 1024L;
            long freeMemoryMB = freeMemory / 1024L / 1024L;
            return freeMemory + " bytes (" + freeMemoryMB + " MB) / " + totalMemory + " bytes (" + totalMemoryMB + " MB) up to " + maxMemory + " bytes (" + maxMemoryMB + " MB)";
        });
        this.theReportCategory.addCrashSectionCallable("JVM Flags", () -> {
            RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
            List<String> list = runtimemxbean.getInputArguments();
            int i = 0;
            StringBuilder stringbuilder = new StringBuilder();

            for (String s : list) {
                if (s.startsWith("-X")) {
                    if (i++ > 0) {
                        stringbuilder.append(" ");
                    }

                    stringbuilder.append(s);
                }
            }

            return String.format("%d total; %s", i, stringbuilder.toString());
        });
        this.theReportCategory.addCrashSectionCallable("IntCache", IntCache::getCacheSizes);

        if (Reflector.FMLCommonHandler_enhanceCrashReport.exists())
        {
            Object object = Reflector.call(Reflector.FMLCommonHandler_instance);
            Reflector.callString(object, Reflector.FMLCommonHandler_enhanceCrashReport, this, this.theReportCategory);
        }
    }

    /**
     * Returns the description of the Crash Report.
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Returns the Throwable object that is the cause for the crash and Crash Report.
     */
    public Throwable getCrashCause()
    {
        return this.cause;
    }

    /**
     * Gets the various sections of the crash report into the given StringBuilder
     */
    public void getSectionsInStringBuilder(StringBuilder builder)
    {
        if ((this.stacktrace == null || this.stacktrace.length <= 0) && this.crashReportSections.size() > 0)
        {
            this.stacktrace = ArrayUtils.subarray(this.crashReportSections.get(0).getStackTrace(), 0, 1);
        }

        if (this.stacktrace != null && this.stacktrace.length > 0)
        {
            builder.append("-- Head --\n");
            builder.append("Stacktrace:\n");

            for (StackTraceElement stacktraceelement : this.stacktrace)
            {
                builder.append("\t").append("at ").append(stacktraceelement.toString());
                builder.append("\n");
            }

            builder.append("\n");
        }

        for (CrashReportCategory crashreportcategory : this.crashReportSections)
        {
            crashreportcategory.appendToStringBuilder(builder);
            builder.append("\n\n");
        }

        this.theReportCategory.appendToStringBuilder(builder);
    }

    public String getCauseStackTraceOrString() {
        StringWriter stringwriter = null;
        PrintWriter printwriter = null;
        Throwable throwable = this.cause;

        if (throwable.getMessage() == null) {
            if (throwable instanceof NullPointerException) {
                throwable = new NullPointerException(this.description);
            } else if (throwable instanceof StackOverflowError) {
                throwable = new StackOverflowError(this.description);
            } else if (throwable instanceof OutOfMemoryError) {
                throwable = new OutOfMemoryError(this.description);
            }

            throwable.setStackTrace(this.cause.getStackTrace());
        }

        String s;

        try {
            stringwriter = new StringWriter();
            printwriter = new PrintWriter(stringwriter);
            throwable.printStackTrace(printwriter);
            s = stringwriter.toString();
        } finally {
            IOUtils.closeQuietly(stringwriter);
            IOUtils.closeQuietly(printwriter);
        }

        return s;
    }

    public String getCompleteReport() {
        if (!this.reported) {
            this.reported = true;
            CrashReporter.onCrashReport(this, this.theReportCategory);
        }

        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("---- Minecraft Crash Report ----\n");
        Reflector.call(Reflector.BlamingTransformer_onCrash, stringbuilder);
        Reflector.call(Reflector.CoreModManager_onCrash, stringbuilder);
        stringbuilder.append("// ");
        stringbuilder.append(getWittyComment());
        stringbuilder.append("\n\n");
        stringbuilder.append("Time: ");
        stringbuilder.append((new SimpleDateFormat()).format(new Date()));
        stringbuilder.append("\n");
        stringbuilder.append("Description: ");
        stringbuilder.append(this.description);
        stringbuilder.append("\n\n");
        stringbuilder.append(this.getCauseStackTraceOrString());
        stringbuilder.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

        for (int i = 0; i < 87; ++i) {
            stringbuilder.append("-");
        }

        stringbuilder.append("\n\n");
        this.getSectionsInStringBuilder(stringbuilder);
        return stringbuilder.toString();
    }

    public File getFile() {
        return this.crashReportFile;
    }

    public boolean saveToFile(File toFile) {
        if (this.crashReportFile != null) {
            return false;
        } else {
            if (toFile.getParentFile() != null) {
                toFile.getParentFile().mkdirs();
            }

            try {
                FileWriter filewriter = new FileWriter(toFile);
                filewriter.write(this.getCompleteReport());
                filewriter.close();
                this.crashReportFile = toFile;
                return true;
            } catch (Throwable throwable) {
                logger.error("Could not save crash report to " + toFile, throwable);
                return false;
            }
        }
    }

    public CrashReportCategory getCategory() {
        return this.theReportCategory;
    }

    public CrashReportCategory makeCategory(String name) {
        return this.makeCategoryDepth(name, 1);
    }

    public CrashReportCategory makeCategoryDepth(String categoryName, int stacktraceLength) {
        CrashReportCategory crashreportcategory = new CrashReportCategory(categoryName);

        if (this.field_85059_f) {
            int i = crashreportcategory.getPrunedStackTrace(stacktraceLength);
            StackTraceElement[] stackTrace = this.cause.getStackTrace();
            StackTraceElement traceElement = null;
            StackTraceElement traceElement1 = null;
            int j = stackTrace.length - i;

            if (j < 0) {
                System.out.println("Negative index in crash report handler (" + stackTrace.length + "/" + i + ")");
            }

            if (0 <= j && j < stackTrace.length) {
                traceElement = stackTrace[j];

                if (stackTrace.length + 1 - i < stackTrace.length) {
                    traceElement1 = stackTrace[stackTrace.length + 1 - i];
                }
            }

            this.field_85059_f = crashreportcategory.firstTwoElementsOfStackTraceMatch(traceElement, traceElement1);

            if (i > 0 && !this.crashReportSections.isEmpty()) {
                CrashReportCategory category = this.crashReportSections.get(this.crashReportSections.size() - 1);
                category.trimStackTraceEntriesFromBottom(i);
            } else if (stackTrace.length >= i && 0 <= j && j < stackTrace.length) {
                this.stacktrace = new StackTraceElement[j];
                System.arraycopy(stackTrace, 0, this.stacktrace, 0, this.stacktrace.length);
            } else {
                this.field_85059_f = false;
            }
        }

        this.crashReportSections.add(crashreportcategory);
        return crashreportcategory;
    }

    private static String getWittyComment() {
        String[] strings = new String[] {"Who set us up the TNT?", "Everything's going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I'm sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don't be sad. I'll do better next time, I promise!", "Don't be sad, have a hug! <3", "I just don't know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn't worry myself about that.", "I bet Cylons wouldn't have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I'm Minecraft, and I'm a crashaholic.", "Ooh. Shiny.", "This doesn't make any sense!", "Why is it breaking :(", "Don't do that.", "Ouch. That hurt :(", "You're mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine."};

        try {
            return strings[(int)(System.nanoTime() % (long)strings.length)];
        } catch (Throwable var2) {
            return "Witty comment unavailable :(";
        }
    }

    public static CrashReport makeCrashReport(Throwable cause, String description) {
        CrashReport crashreport;

        if (cause instanceof ReportedException) {
            crashreport = ((ReportedException)cause).getCrashReport();
        } else {
            crashreport = new CrashReport(description, cause);
        }

        return crashreport;
    }
}
