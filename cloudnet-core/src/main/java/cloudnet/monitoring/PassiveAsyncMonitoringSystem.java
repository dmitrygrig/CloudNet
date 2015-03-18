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
package cloudnet.monitoring;

import cloudnet.monitoring.model.CloudHistory;
import cloudnet.monitoring.model.DatacenterHistory;
import cloudnet.monitoring.model.PmHistory;
import cloudnet.monitoring.model.VmHistory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class PassiveAsyncMonitoringSystem extends PassiveMonitoringSystem {

    private ExecutorService executorService;

    public PassiveAsyncMonitoringSystem(HistoryWriter writer) {
        super(writer);
    }

    public PassiveAsyncMonitoringSystem(HistoryWriter writer, ExecutorService executorService) {
        super(writer);
        this.executorService = executorService;
    }

    public ExecutorService getExecutorService() {
        return executorService != null ? executorService : (executorService = Executors.newCachedThreadPool());
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    protected void writeCloudHistory(CloudHistory hist) {
        getExecutorService().submit(() -> writer.saveCloudHistory(hist));
    }

    @Override
    protected void writeDcHistory(DatacenterHistory hist) {
        getExecutorService().submit(() -> writer.saveDcHistory(hist));
    }

    @Override
    protected void writePmHistory(PmHistory hist) {
        getExecutorService().submit(() -> writer.savePmHistory(hist));
    }

    @Override
    protected void writeVmHistory(VmHistory hist) {
        getExecutorService().submit(() -> writer.saveVmHistory(hist));
    }

}
