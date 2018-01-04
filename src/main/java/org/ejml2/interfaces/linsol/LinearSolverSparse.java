/*
 * Copyright (c) 2009-2017, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Efficient Java Matrix Library (EJML).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ejml2.interfaces.linsol;

import org.ejml2.data.Matrix;


/**

 * @author Peter Abeles
 */
// Ideas. Base linear solver will have two inputs
//    Matrix type specific solvers, e.g. LinearSolver_DDRM implements LinearSolverDense<DMatrixRMaj,DMatrixRMaj>
// have sparse solver extend LinearSolverDense but add functions to it
public interface LinearSolverSparse<S extends Matrix, D extends Matrix> extends LinearSolver<S,D> {

    /**
     * <p>Save results from structural analysis step. This can reduce computations of a matrix with the exactly same
     * non-zero pattern is decomposed in the future.  If a matrix has yet to be processed then the structure of
     * the next matrix is saved. If a matrix has already been processed then the structure of the most recently
     * processed matrix will be saved.</p>
     */
    void lockStructure();

    /**
     * Checks to see if the structure is locked.
     * @return true if locked or false if not locked.
     */
    boolean isStructureLocked();

}
