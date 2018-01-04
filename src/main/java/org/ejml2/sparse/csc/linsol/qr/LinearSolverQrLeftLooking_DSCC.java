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

package org.ejml2.sparse.csc.linsol.qr;

import org.ejml2.data.DGrowArray;
import org.ejml2.data.DMatrixRMaj;
import org.ejml2.data.DMatrixSparseCSC;
import org.ejml2.interfaces.decomposition.DecompositionInterface;
import org.ejml2.interfaces.linsol.LinearSolverSparse;
import org.ejml2.sparse.csc.CommonOps_DSCC;
import org.ejml2.sparse.csc.decomposition.qr.QrHelperFunctions_DSCC;
import org.ejml2.sparse.csc.decomposition.qr.QrLeftLookingDecomposition_DSCC;
import org.ejml2.sparse.csc.misc.TriangularSolver_DSCC;

import static org.ejml2.sparse.csc.misc.TriangularSolver_DSCC.adjust;

/**
 * Sparse linear solver implemented using {@link QrLeftLookingDecomposition_DSCC}.
 *
 * @author Peter Abeles
 */
public class LinearSolverQrLeftLooking_DSCC implements LinearSolverSparse<DMatrixSparseCSC,DMatrixRMaj> {

    private QrLeftLookingDecomposition_DSCC qr;
    private int m,n;

    private DGrowArray gb = new DGrowArray();
    private DGrowArray gbp = new DGrowArray();
    private DGrowArray gx = new DGrowArray();

    public LinearSolverQrLeftLooking_DSCC(QrLeftLookingDecomposition_DSCC qr) {
        this.qr = qr;
    }

    @Override
    public boolean setA(DMatrixSparseCSC A) {
        if( A.numCols > A.numRows )
            throw new IllegalArgumentException("Can't handle wide matrices");
        this.m = A.numRows;
        this.n = A.numCols;
        return qr.decompose(A) && !qr.isSingular();
    }

    @Override
    public /**/double quality() {
        return TriangularSolver_DSCC.qualityTriangular(qr.getR());
    }

    @Override
    public void lockStructure() {
        qr.lockStructure();
    }

    @Override
    public boolean isStructureLocked() {
        return qr.isStructureLocked();
    }

    @Override
    public void solve(DMatrixRMaj B, DMatrixRMaj X) {
        double[] b = adjust(gb,B.numRows);
        double[] bp = adjust(gbp,B.numRows);
        double[] x = adjust(gx,X.numRows);

        int pinv[] = qr.getStructure().getPinv();

        // process each column in X and B individually
        for (int colX = 0; colX < X.numCols; colX++) {
            int index = colX;
            for( int i = 0; i < B.numRows; i++ , index += X.numCols ) b[i] = B.data[index];

            // apply row pivots
            CommonOps_DSCC.permuteInv(pinv, b, bp, m);

            // apply Householder reflectors
            for (int j = 0; j < n; j++) {
                QrHelperFunctions_DSCC.applyHouseholder(qr.getV(),j,qr.getBeta(j),bp);
            }
            // Solve for R*x = b
            TriangularSolver_DSCC.solveU(qr.getR(),bp);

            // undo the permutation
            double out[];
            if( qr.isFillPermutated()) {
                CommonOps_DSCC.permute(qr.getFillPermutation(), bp, x, X.numRows);
                out = x;
            } else {
                out = bp;
            }

            index = colX;
            for( int i = 0; i < X.numRows; i++ , index += X.numCols ) X.data[index] = out[i];
        }
    }

    @Override
    public boolean modifiesA() {
        return qr.inputModified();
    }

    @Override
    public boolean modifiesB() {
        return false;
    }

    @Override
    public <D extends DecompositionInterface> D getDecomposition() {
        return (D)qr;
    }
}
