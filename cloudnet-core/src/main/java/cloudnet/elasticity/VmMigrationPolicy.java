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
package cloudnet.elasticity;

import cloudnet.core.Vm;

/**
 *
 * @author Dmytro
 */
public interface VmMigrationPolicy {

    /**
     * Returns whether the specified Vm should be considered as a candidate for
     * migration.
     *
     * @param vm Target Vm
     * @return True, if should be considered as a candidate for migration.
     */
    boolean shouldBeMigrated(Vm vm);
}
