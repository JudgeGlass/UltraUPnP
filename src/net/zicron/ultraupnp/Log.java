package net.zicron.ultraupnp;


/*
 * Copyright 2020 Hunter Wilcox
 * Copyright 2020 Zicron-Technologies
 *
 * This file is part of UltraUPNP.
 *
 * UltraUPNP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraUPNP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UltraUPNP.  If not, see <http://www.gnu.org/licenses/>.
 */


import javafx.application.Platform;
import net.zicron.ultraupnp.gui.MainWindow;

public class Log {
    public static void info(String message){
        System.out.println("[UltraUPNP][INFO] " + message);
        if(MainWindow.currentMainWindow != null){
            MainWindow.currentMainWindow.appendToLog("[UltraUPNP][INFO] " + message + "\n");
        }
    }

    public static void warn(String message){
        System.out.println("[UltraUPNP][WARN] " + message);
        if(MainWindow.currentMainWindow != null){
            MainWindow.currentMainWindow.appendToLog("[UltraUPNP][WARN] " + message + "\n");
        }
    }

    public static void error(String message){
        System.err.println("[UltraUPNP][ERROR] " + message);
        if(MainWindow.currentMainWindow != null){
            MainWindow.currentMainWindow.appendToLog("[UltraUPNP][ERROR] " + message + "\n");
        }
    }

    public static void debug(String message) {
        if(UltraUPnP.IS_BETA) {
            System.out.println("[DEBUG] " + message);
            if (MainWindow.currentMainWindow != null) {
                MainWindow.currentMainWindow.appendToLog("[DEBUG] " + message + "\n");
            }
        }
    }
}
