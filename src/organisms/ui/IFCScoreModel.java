
package organisms.ui;

import java.io.Serializable;

public interface IFCScoreModel extends Serializable {

    public double singleWinner(int __numplayers)                    throws Exception;
    public double multipleWinners(int __numplayers, int __numtied)  throws Exception;
}
