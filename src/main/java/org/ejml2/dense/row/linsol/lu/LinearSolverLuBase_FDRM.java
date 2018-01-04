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

package org.ejml2.dense.row.linsol.lu;

import org.ejml2.data.FMatrixRMaj;
import org.ejml2.dense.row.decomposition.lu.LUDecompositionBase_FDRM;
import org.ejml2.dense.row.linsol.LinearSolverAbstract_FDRM;


/**
 * @author Peter Abeles
 */
public abstract class LinearSolverLuBase_FDRM extends LinearSolverAbstract_FDRM {

    protected LUDecompositionBase_FDRM decomp;

    public LinearSolverLuBase_FDRM(LUDecompositionBase_FDRM decomp) {
        this.decomp = decomp;

    }

    @Override
    public boolean setA(FMatrixRMaj A) {
        _setA(A);

        return decomp.decompose(A);
    }

    @Override
    public /**/double quality() {
        return decomp.quality();
    }

    @Override
    public void invert(FMatrixRMaj A_inv) {
        float []vv = decomp._getVV();
        FMatrixRMaj LU = decomp.getLU();

        if( A_inv.numCols != LU.numCols || A_inv.numRows != LU.numRows )
            throw new IllegalArgumentException("Unexpected matrix dimension");

        int n = A.numCols;

        float dataInv[] = A_inv.data;

        for( int j = 0; j < n; j++ ) {
            // don't need to change inv into an identity matrix before hand
            for( int i = 0; i < n; i++ ) vv[i] = i == j ? 1 : 0;
            decomp._solveVectorInternal(vv);
//            for( int i = 0; i < n; i++ ) dataInv[i* n +j] = vv[i];
            int index = j;
            for( int i = 0; i < n; i++ , index += n) dataInv[ index ] = vv[i];
        }
    }

    /**
     * This attempts to improve upon the solution generated by account
     * for numerical imprecisions.  See numerical recipes for more information.  It
     * is assumed that solve has already been run on 'b' and 'x' at least once.
     *
     * @param b A matrix. Not modified.
     * @param x A matrix. Modified.
     */
    public void improveSol(FMatrixRMaj b , FMatrixRMaj x )
    {
        if( b.numCols != x.numCols ) {
            throw new IllegalArgumentException("bad shapes");
        }

        float dataA[] = A.data;
        float dataB[] = b.data;
        float dataX[] = x.data;

        final int nc = b.numCols;
        final int n = b.numCols;

        float []vv = decomp._getVV();

//        BigDecimal sdp = new BigDecimal(0);
        for( int k = 0; k < nc; k++ ) {
            for( int i = 0; i < n; i++ ) {
                // *NOTE* in the book this is a long float.  extra precision might be required
                float sdp = -dataB[ i * nc + k];
//                BigDecimal sdp = new BigDecimal(-dataB[ i * nc + k]);
                for( int j = 0; j < n; j++ ) {
                    sdp += dataA[i* n +j] * dataX[ j * nc + k];
//                    sdp = sdp.add( BigDecimal.valueOf(dataA[i* n +j] * dataX[ j * nc + k]));
                }
                vv[i] = sdp;
//                vv[i] = sdp.floatValue();
            }
            decomp._solveVectorInternal(vv);
            for( int i = 0; i < n; i++ ) {
                dataX[i*nc + k] -= vv[i];
            }
        }
    }

    @Override
    public boolean modifiesA() {
        return false;
    }

    @Override
    public boolean modifiesB() {
        return false;
    }

    @Override
    public LUDecompositionBase_FDRM getDecomposition() {
        return decomp;
    }
}