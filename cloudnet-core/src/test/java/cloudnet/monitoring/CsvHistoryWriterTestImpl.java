/*
 * Copyright (C) 2014 Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cloudnet.monitoring;

import java.io.FileWriter;
import java.util.List;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class CsvHistoryWriterTestImpl extends CsvHistoryWriter {

    public CsvHistoryWriterTestImpl(String cloudHistoryFilename, String dcHistoryFilename, String pmHistoryFilename, String vmHistoryFilename) {
        super(cloudHistoryFilename, dcHistoryFilename, pmHistoryFilename, vmHistoryFilename, 0, true);
    }

    @Override
    protected void writeHistoryIntoCsvFile(String filename, String header, List<String> data) {
        this.calledFilename = filename;
        this.calledHeader = header;
        this.calledData = data.get(0);
    }

    private String calledFilename;
    private String calledHeader;
    private String calledData;

    public String getCalledFilename() {
        return calledFilename;
    }

    public String getCalledHeader() {
        return calledHeader;
    }

    public String getCalledData() {
        return calledData;
    }

}
