package structlab.trace;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvariantResultTest {

  @Test
  void fromBooleanTrue() {
    assertEquals(InvariantResult.PASSED, InvariantResult.fromBoolean(true));
  }

  @Test
  void fromBooleanFalse() {
    assertEquals(InvariantResult.FAILED, InvariantResult.fromBoolean(false));
  }

  @Test
  void allValuesExist() {
    assertEquals(3, InvariantResult.values().length);
  }
}
