package communication;

import java.io.Serializable;

/**
 * This enum represents headers of protocols of Protocol class.
 */
public enum Header implements Serializable {
    CONNECT,
    CONNECTED,
    CREATEGAME,
    CREATEDGAME,
    GAMEUPDATED,
    LEAVEGAME,
    LEAVELOBBY,
    LOGIN,
    LOGGEDIN,
    LOGOUT,
    LOGGEDOUT,
    JOINGAME,
    JOINEDGAME,
    PLAYERJOINED,
    REGISTER,
    REGISTERED,
    STARTGAME,
    STARTEDGAME,
    UPDATEGAME,
    FAIL,
    STOP,
    NULLCOMMAND
}