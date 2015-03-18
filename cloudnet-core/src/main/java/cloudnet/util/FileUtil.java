/*
 *
 * Copyright (C) 2015 Dmytro Grygorenko <dmitrygrig@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cloudnet.util;

import java.io.File;
import java.io.FileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig@gmail.com>
 */
public class FileUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    public static void writeToFile(String filename, String data, boolean append) {
      
        File file = new File(filename);
        boolean exists = file.exists();
        if (!exists) {
            file.getParentFile().mkdirs();
        }

        try (FileWriter writer = new FileWriter(filename, append)) {
            writer.append(data + "\r\n");
        } catch (Exception e) {
            LOGGER.error("writeToFile", e);
        }
    }
}
