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
package cloudnet.sla;

import cloudnet.core.Sla;
import cloudnet.core.SlaLevel;
import cloudnet.core.Vm;
import cloudnet.util.Ensure;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class VmAvailabilityBasedSla implements Sla {

    private final VmSlaCummulativeDowntime cummSla;
    private final VmSlaOverallDowntime overallSla;

    public VmAvailabilityBasedSla(VmSlaOverallDowntime overallSla, VmSlaCummulativeDowntime cummSla) {
        Ensure.NotNull(overallSla, "overallSla");
        Ensure.NotNull(cummSla, "cummSla");
        this.cummSla = cummSla;
        this.overallSla = overallSla;
    }

    @Override
    public double getPenalty() {
        return cummSla.getPenalty() + overallSla.getPenalty();
    }

    public double getCummViolationRate() {
        return cummSla.getViolationRate();
    }

    public double getOverallViolationRate() {
        return overallSla.getViolationRate();
    }
    
    public void setVm(Vm vm){
        cummSla.setVm(vm);
        overallSla.setVm(vm);
    }

    @Override
    public SlaLevel getLevel() {
        return overallSla.getLevel();
    }

}
