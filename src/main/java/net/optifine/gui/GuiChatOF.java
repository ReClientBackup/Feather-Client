package net.optifine.gui;

import com.murengezi.minecraft.client.gui.Chat.ChatScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.src.Config;
import net.optifine.shaders.Shaders;

public class GuiChatOF extends ChatScreen {
   private static final String CMD_RELOAD_SHADERS = "/reloadShaders";
   private static final String CMD_RELOAD_CHUNKS = "/reloadChunks";

   public GuiChatOF(ChatScreen guiChat) {
      super(GuiVideoSettings.getGuiChatText(guiChat));
   }

   public void sendChatMessage(String msg) {
      if(this.checkCustomCommand(msg)) {
         getMc().inGameScreen.getChatGUI().addToSentMessages(msg);
      } else {
         super.sendChatMessage(msg);
      }
   }

   private boolean checkCustomCommand(String msg) {
      if(msg == null) {
         return false;
      } else {
         msg = msg.trim();
         if(msg.equals("/reloadShaders")) {
            if(Config.isShaders()) {
               Shaders.uninit();
               Shaders.loadShaderPack();
            }

            return true;
         } else if(msg.equals("/reloadChunks")) {
            getMc().renderGlobal.loadRenderers();
            return true;
         } else {
            return false;
         }
      }
   }
}
