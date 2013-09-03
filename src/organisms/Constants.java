package organisms;

import java.io.Serializable;

public interface Constants {

    public static final String _CERROR_STRING = "Error: ";

    public static final int MIN_EXTSTATE = 0;
    public static final int MAX_EXTSTATE = 255;
	
    public static final int STAYPUT    = 0;
    public static final int WEST       = 1;
    public static final int EAST       = 2;
    public static final int NORTH      = 3;
    public static final int SOUTH      = 4;

    public static final int REPRODUCE  = 5;

    public static final int[] DIRECTIONS = { STAYPUT, WEST, EAST, NORTH, SOUTH };
    public static final int[] _CXTrans     = { 0, -1, 1, 0, 0};
    public static final int[] _CYTrans     = { 0, 0, 0, -1, 1};
}
