//ScopeSettings.java
//Used to store settings for the oscilloscope display

//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License as
//published by the Free Software Foundation; either version 3 of the
//License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful, but
//WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//General Public License for more details:
//http://www.gnu.org/licenses/gpl.txt

//Copyright 2009 Brian Satzinger

package scopeduino.display;

/**
 *
 * @author brian
 */
public class ScopeSettings {
    static float backr, backg, backb, z;

    static boolean hardFirstTrace = false;

    static float amp;

    static boolean paused = false;

    static int hZoom = 256;
    static int hPan = 0;

    //Cursor Settings
    static float hc1 = -1.0f;
    static float hc2 = -1.0f;
    static boolean enableCursors = false;
    static float cursorWidth = 1.5f;
    //cursor color
    static float hcursorR = 1.0f;
    static float hcursorG = 1.0f;
    static float hcursorB = 1.0f;
    static float samplePeriod = .001f;

}
