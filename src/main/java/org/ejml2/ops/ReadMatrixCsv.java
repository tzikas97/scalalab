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

package org.ejml2.ops;

import org.ejml2.data.DMatrixRMaj;
import org.ejml2.data.Matrix;
import org.ejml2.data.ZMatrixRMaj;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * Reads in a matrix that is in a column-space-value (CSV) format.
 *
 * @author Peter Abeles
 */
public class ReadMatrixCsv extends ReadCsv {

    /**
     * Specifies where input comes from.
     *
     * @param in Where the input comes from.
     */
    public ReadMatrixCsv(InputStream in) {
        super(in);
    }

    /**
     * Reads in a DMatrixRMaj from the IO stream.
     * @return DMatrixRMaj
     * @throws IOException If anything goes wrong.
     */
    public <M extends Matrix>M read() throws IOException {
        List<String> words = extractWords();
        if( words.size() != 3 )
            throw new IOException("Unexpected number of words on first line.");

        int numRows = Integer.parseInt(words.get(0));
        int numCols = Integer.parseInt(words.get(1));
        boolean real = words.get(2).compareToIgnoreCase("real") == 0;

        if( numRows < 0 || numCols < 0)
            throw new IOException("Invalid number of rows and/or columns: "+numRows+" "+numCols);

        if( real )
            return (M)readReal(numRows, numCols);
        else
            return (M)readComplex(numRows, numCols);
    }

    /**
     * Reads in a DMatrixRMaj from the IO stream where the user specifies the matrix dimensions.
     *
     * @param numRows Number of rows in the matrix
     * @param numCols Number of columns in the matrix
     * @return DMatrixRMaj
     * @throws IOException
     */
    public DMatrixRMaj readReal(int numRows, int numCols) throws IOException {

        DMatrixRMaj A = new DMatrixRMaj(numRows,numCols);

        for( int i = 0; i < numRows; i++ ) {
            List<String> words = extractWords();
            if( words == null )
                throw new IOException("Too few rows found. expected "+numRows+" actual "+i);

            if( words.size() != numCols )
                throw new IOException("Unexpected number of words in column. Found "+words.size()+" expected "+numCols);
            for( int j = 0; j < numCols; j++ ) {
                A.set(i,j,Double.parseDouble(words.get(j)));
            }
        }

        return A;
    }

    /**
     * Reads in a ZMatrixRMaj from the IO stream where the user specifies the matrix dimensions.
     *
     * @param numRows Number of rows in the matrix
     * @param numCols Number of columns in the matrix
     * @return DMatrixRMaj
     * @throws IOException
     */
    public ZMatrixRMaj readComplex(int numRows, int numCols) throws IOException {

        ZMatrixRMaj A = new ZMatrixRMaj(numRows,numCols);

        int wordsCol = numCols*2;

        for( int i = 0; i < numRows; i++ ) {
            List<String> words = extractWords();
            if( words == null )
                throw new IOException("Too few rows found. expected "+numRows+" actual "+i);

            if( words.size() != wordsCol )
                throw new IOException("Unexpected number of words in column. Found "+words.size()+" expected "+wordsCol);
            for( int j = 0; j < wordsCol; j += 2 ) {

                double real = Double.parseDouble(words.get(j));
                double imaginary = Double.parseDouble(words.get(j+1));

                A.set(i, j, real, imaginary);
            }
        }

        return A;
    }
}
