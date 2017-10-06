/*
 * Copyright 2017 EMBL - European Bioinformatics Institute
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

package uk.ac.ebi.eva.commons.core.models;

import org.apache.commons.lang3.StringUtils;

import static java.lang.Math.max;

/**
 * Class that stores a Variant coordinates and alleles, normalizing and left aligning them.
 * <p>
 * This task comprises 2 steps: removing the trailing bases that are
 * identical in both alleles, then the leading identical bases.
 * <p>
 * It is left aligned because the trailing bases are removed before the leading ones, implying a normalization where
 * the position is moved the least possible from its original location.
 */
public class VariantKeyFields {

    private final String chromosome;

    private int start;

    private int end;

    private String reference;

    private String alternate;

    public VariantKeyFields(String chromosome, int position, String reference, String alternate) {
        if (reference.equals(alternate)) {
            throw new IllegalArgumentException("One alternate allele is identical to the reference. Variant found as: "
                                                       + chromosome + ":" + position + ":" + reference + ">" + alternate);
        }

        this.chromosome = chromosome;

        // remove common trailing bases
        int nucleotidesToRemoveInTheRight = getIndexOfLastDifferentNucleotide(reference, alternate);
        String rightTrimmedReference = reference.substring(0, reference.length() - nucleotidesToRemoveInTheRight);
        String rightTrimmedAlternate = alternate.substring(0, alternate.length() - nucleotidesToRemoveInTheRight);

        // remove common leading bases
        int nucleotidesToRemoveInTheLeft = StringUtils.indexOfDifference(rightTrimmedReference, rightTrimmedAlternate);
        this.reference = rightTrimmedReference.substring(nucleotidesToRemoveInTheLeft);
        this.alternate = rightTrimmedAlternate.substring(nucleotidesToRemoveInTheLeft);

        // calculate start and end
        start = position + nucleotidesToRemoveInTheLeft;
        end = calculateEnd(position, rightTrimmedReference, rightTrimmedAlternate);
    }

    private int getIndexOfLastDifferentNucleotide(String reference, String alternate) {
        String refReversed = StringUtils.reverse(reference);
        String altReversed = StringUtils.reverse(alternate);
        return StringUtils.indexOfDifference(refReversed, altReversed);
    }

    private int calculateEnd(int position, String reference, String alternate) {
        int length = max(reference.length(), alternate.length());
        return position + length - 1;
    }

    public String getChromosome() {
        return chromosome;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getReference() {
        return reference;
    }

    public String getAlternate() {
        return alternate;
    }
}