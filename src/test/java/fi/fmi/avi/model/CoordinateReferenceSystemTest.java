package fi.fmi.avi.model;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

public class CoordinateReferenceSystemTest {
    @Test
    public void checkSanity_given_empty_name_throws_IllegalStateException() {
        final AbstractTestCoordinateReferenceSystem crs = new AbstractTestCoordinateReferenceSystem() {
            @Override
            public String getName() {
                return "";
            }
        };
        assertThatIllegalStateException()//
                .isThrownBy(crs::checkSanity)//
                .withMessageContaining("name");
    }

    @Test
    public void checkSanity_given_nonpositive_dimension_throws_IllegalStateException() {
        final AbstractTestCoordinateReferenceSystem crs = new AbstractTestCoordinateReferenceSystem() {
            @Override
            public Optional<Integer> getDimension() {
                return Optional.of(0);
            }
        };
        assertThatIllegalStateException()//
                .isThrownBy(crs::checkSanity)//
                .withMessageContaining("dimension")//
                .withMessageContaining("0");
    }

    @Test
    public void checkSanity_given_axisLabels_differing_with_dimension_throws_IllegalStateException() {
        final AbstractTestCoordinateReferenceSystem crs = new AbstractTestCoordinateReferenceSystem() {
            @Override
            public Optional<Integer> getDimension() {
                return Optional.of(2);
            }

            @Override
            public List<String> getAxisLabels() {
                return Collections.singletonList("Lat");
            }
        };
        assertThatIllegalStateException()//
                .isThrownBy(crs::checkSanity)//
                .withMessageContaining("axisLabels")//
                .withMessageContaining("dimension")//
                .withMessageContaining("2")//
                .withMessageContaining("Lat");
    }

    @Test
    public void checkSanity_given_uomLabels_differing_with_dimension_throws_IllegalStateException() {
        final AbstractTestCoordinateReferenceSystem crs = new AbstractTestCoordinateReferenceSystem() {
            @Override
            public Optional<Integer> getDimension() {
                return Optional.of(2);
            }

            @Override
            public List<String> getUomLabels() {
                return Collections.singletonList("deg");
            }
        };
        assertThatIllegalStateException()//
                .isThrownBy(crs::checkSanity)//
                .withMessageContaining("uomLabels")//
                .withMessageContaining("dimension")//
                .withMessageContaining("2")//
                .withMessageContaining("deg");
    }

    @Test
    public void checkSanity_given_no_dimension_but_differing_axis_and_uom_labelsthrows_IllegalStateException() {
        final AbstractTestCoordinateReferenceSystem crs = new AbstractTestCoordinateReferenceSystem() {
            @Override
            public List<String> getAxisLabels() {
                return Arrays.asList("Lat", "Lon");
            }

            @Override
            public List<String> getUomLabels() {
                return Collections.singletonList("deg");
            }
        };
        assertThatIllegalStateException()//
                .isThrownBy(crs::checkSanity)//
                .withMessageContaining("axisLabels")//
                .withMessageContaining("uomLabels")//
                .withMessageContaining("Lat")//
                .withMessageContaining("Lon")//
                .withMessageContaining("deg");
    }

    private static abstract class AbstractTestCoordinateReferenceSystem implements CoordinateReferenceSystem {
        @Override
        public String getName() {
            return AviationCodeListUser.CODELIST_VALUE_EPSG_4326;
        }

        @Override
        public Optional<Integer> getDimension() {
            return Optional.empty();
        }

        @Override
        public List<String> getAxisLabels() {
            return Collections.emptyList();
        }

        @Override
        public List<String> getUomLabels() {
            return Collections.emptyList();
        }
    }
}
