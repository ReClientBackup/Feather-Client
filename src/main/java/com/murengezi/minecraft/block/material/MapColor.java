package com.murengezi.minecraft.block.material;

public class MapColor {
    
    public static final MapColor[] mapColorArray = new MapColor[64];
    public static final MapColor airColor = new MapColor(0, 0), grassColor = new MapColor(1, 8368696), sandColor = new MapColor(2, 16247203), clothColor = new MapColor(3, 13092807), tntColor = new MapColor(4, 16711680), iceColor = new MapColor(5, 10526975), ironColor = new MapColor(6, 10987431), foliageColor = new MapColor(7, 31744), snowColor = new MapColor(8, 16777215), clayColor = new MapColor(9, 10791096), dirtColor = new MapColor(10, 9923917), stoneColor = new MapColor(11, 7368816), waterColor = new MapColor(12, 4210943), woodColor = new MapColor(13, 9402184), quartzColor = new MapColor(14, 16776437), adobeColor = new MapColor(15, 14188339), magentaColor = new MapColor(16, 11685080), lightBlueColor = new MapColor(17, 6724056), yellowColor = new MapColor(18, 15066419), limeColor = new MapColor(19, 8375321), pinkColor = new MapColor(20, 15892389), grayColor = new MapColor(21, 5000268), silverColor = new MapColor(22, 10066329), cyanColor = new MapColor(23, 5013401), purpleColor = new MapColor(24, 8339378), blueColor = new MapColor(25, 3361970), brownColor = new MapColor(26, 6704179), greenColor = new MapColor(27, 6717235), redColor = new MapColor(28, 10040115), blackColor = new MapColor(29, 1644825), goldColor = new MapColor(30, 16445005), diamondColor = new MapColor(31, 6085589), lapisColor = new MapColor(32, 4882687), emeraldColor = new MapColor(33, 55610), obsidianColor = new MapColor(34, 8476209), netherrackColor = new MapColor(35, 7340544);

    public int colorValue;

    public final int colorIndex;

    private MapColor(int index, int color) {
        if (index >= 0 && index <= 63) {
            this.colorIndex = index;
            this.colorValue = color;
            mapColorArray[index] = this;
        } else {
            throw new IndexOutOfBoundsException("Map colour ID must be between 0 and 63 (inclusive)");
        }
    }

    public int func_151643_b(int p_151643_1_) {
        int i = 220;

        if (p_151643_1_ == 3) {
            i = 135;
        }

        if (p_151643_1_ == 2) {
            i = 255;
        }

        if (p_151643_1_ == 1) {
            i = 220;
        }

        if (p_151643_1_ == 0) {
            i = 180;
        }

        int j = (this.colorValue >> 16 & 255) * i / 255;
        int k = (this.colorValue >> 8 & 255) * i / 255;
        int l = (this.colorValue & 255) * i / 255;
        return -16777216 | j << 16 | k << 8 | l;
    }
    
}
