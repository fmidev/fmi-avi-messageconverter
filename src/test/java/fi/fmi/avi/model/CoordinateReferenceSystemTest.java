package fi.fmi.avi.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CoordinateReferenceSystemTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void checkSanity_given_empty_name_throws_IllegalStateException() {
        final AbstractTestCoordinateReferenceSystem crs = new AbstractTestCoordinateReferenceSystem() {
            @Override
            public String getName() {
                return "";
            }
        };
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("name");
        crs.checkSanity();
    }

    @Test
    public void checkSanity_given_nonpositive_dimension_throws_IllegalStateException() {
        final AbstractTestCoordinateReferenceSystem crs = new AbstractTestCoordinateReferenceSystem() {
            @Override
            public Optional<Integer> getDimension() {
                return Optional.of(0);
            }
        };
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("dimension");
        thrown.expectMessage("0");
        crs.checkSanity();
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
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("axisLabels");
        thrown.expectMessage("dimension");
        thrown.expectMessage("2");
        thrown.expectMessage("Lat");
        crs.checkSanity();
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
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("uomLabels");
        thrown.expectMessage("dimension");
        thrown.expectMessage("2");
        thrown.expectMessage("deg");
        crs.checkSanity();
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
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("axisLabels");
        thrown.expectMessage("uomLabels");
        thrown.expectMessage("Lat");
        thrown.expectMessage("Lon");
        thrown.expectMessage("deg");
        crs.checkSanity();
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
