package eod;

import eod.event.ObjectEnterEvent;
import eod.exceptions.MoveInvalidException;
import eod.param.PointParam;
import eod.snapshots.Snapshotted;
import eod.warObject.Status;
import eod.warObject.WarObject;
import eod.warObject.character.abstraction.Character;

import java.awt.*;
import java.util.ArrayList;

public class Gameboard implements Snapshotted<Gameboard.Snapshot>, GameObject {
    // Player A's base is (0,y) ~ (firstLine-1, y)
    // Player A's front = (1,0)
    public static final int boardSize = 8;
    public static final int firstLine = 2, secondLine = 6;
    public static final int middle = 4;
    private WarObject[][] board = new Character[boardSize][boardSize];

    public <T extends WarObject> T getObjectOn(int x, int y) throws IllegalArgumentException {
        if(!hasObjectOn(x, y)) {
            throw new IllegalArgumentException("There's no object on ("+x+", "+y+").");
        }
        try {
            return (T) board[x][y];
        } catch (Exception e) {
            throw new IllegalArgumentException("the object on ("+x+", "+y+") doesn't match the type you want.");
        }
    }

    public static boolean inBounds(Point p) {
        return inBounds(p.x, p.y);
    }

    public static boolean inBounds(int x, int y) {
        return inBounds(x) && inBounds(y);
    }

    public static boolean inBounds(int n) {
        return n >= 0 && n < boardSize;
    }

    public boolean hasObjectOn(int x, int y) throws ArrayIndexOutOfBoundsException {
        if(!inBounds(x, y)) {
            throw new ArrayIndexOutOfBoundsException("Trying to find an element at ("+x+", "+y+") on the board.");
        }
        return board[x][y]!=null;
    }

    public void moveObject(Point from, Point to) throws MoveInvalidException, ArrayIndexOutOfBoundsException {
        if(!inBounds(to)) {
            throw new ArrayIndexOutOfBoundsException("Trying to move a character to ("+to.x+", "+to.y+").");
        }

        if(board[to.x][to.y] != null) {
            throw new MoveInvalidException("There's already a character on ("+to.x+", "+to.y+").");
        }
        board[to.x][to.y] = board[from.x][from.y];
        board[from.x][from.y] = null;
    }

    public void removeObject(WarObject object) throws IllegalArgumentException {
        Point position = object.getPosition();
        int x = position.x;
        int y = position.y;
        removeObject(x, y);
    }

    public void removeObject(int x, int y) throws IllegalArgumentException {
        if(!inBounds(x, y)) {
            throw new IllegalArgumentException("Trying to remove a character at ("+x+", "+y+").");
        }
        if(board[x][y] == null) {
            throw new IllegalArgumentException("There's nothing at ("+x+", "+y+") on the board, cannot remove.");
        }
        board[x][y] = null;
    }

    public BoardPosition getPosition(Player player, Point p) {
        if(player.isPlayerA()) {
            if(p.x < firstLine) {
                return BoardPosition.SELF_BASE;
            } else if(p.x < middle) {
                return BoardPosition.SELF_CONFLICT;
            } else if(p.x < secondLine) {
                return BoardPosition.ENEMY_CONFLICT;
            } else {
                return BoardPosition.ENEMY_BASE;
            }
        } else {
            if(p.x >= secondLine) {
                return BoardPosition.SELF_BASE;
            } else if(p.x >= middle) {
                return BoardPosition.SELF_CONFLICT;
            } else if(p.x >= firstLine) {
                return BoardPosition.ENEMY_CONFLICT;
            } else {
                return BoardPosition.ENEMY_BASE;
            }
        }
    }

    public ArrayList<Point> getPlayersConflict(Player player, PointParam param) {
        int iMin, iMax;
        if(player.isPlayerA()) {
            iMin = firstLine;
            iMax = middle;
        } else {
            iMin = middle;
            iMax = secondLine;
        }

        ArrayList<Point> r = new ArrayList<>();

        for(int i = iMin;i < iMax;i++) {
            for(int j = 0;j < boardSize;j++) {
                decideAddPoint(new Point(i, j), r, param);
            }
        }

        return r;
    }

    public ArrayList<Point> allSpaces(Point at, PointParam param) {
        // If the x value is smaller than 0, return the whole board.
        int iMin, iMax;
        if(at.x < 0) {
            iMin = 0;
            iMax = boardSize;
        } else if(at.x < firstLine) {
            iMin = 0;
            iMax = firstLine;
        } else if(at.x < secondLine) {
            iMin = firstLine;
            iMax = secondLine;
        } else {
            iMin = secondLine;
            iMax = boardSize;
        }

        ArrayList<Point> points = new ArrayList<>();

        for(int i = iMin;i < iMax;i++) {
            for(int j = 0;j < boardSize;j++) {
                decideAddPoint(new Point(i, j), points, param);
            }

        }
        return points;
    }

    public ArrayList<Point> getSurrounding(Point p, PointParam param) {
        if(param.range == 0) {
            throw new IllegalArgumentException("You forgot to set the range parameter.");
        }
        ArrayList<Point> points = new ArrayList<>();
        for (int x = p.x - param.range;x <= p.x + param.range;x++) {
            if(!inBounds(x)) {
                continue;
            }
            for (int y = p.y - param.range;y <= p.y + param.range;y++) {
                if (!inBounds(y)) {
                    continue;
                }
                decideAddPoint(p, points, param);
            }
        }
        return points;
    }

    private void decideAddPoint(Point p, ArrayList<Point> points, PointParam param) {
        if(hasObjectOn(p.x, p.y)) {
            WarObject object = getObjectOn(p.x, p.y);
            if(!param.emptySpace) {
                boolean clean = true;
                for(Status status:param.excludeObjectStatus) {
                    if(object.hasStatus(status)) {
                        clean = false;
                        break;
                    }
                }
                if(clean) {
                    points.add(p);
                }
            }
        } else {
            points.add(p);
        }
    }

    public ArrayList<Point> get4Ways(Point p, PointParam param) {
        // the return list DOESN'T contain p.
        ArrayList<Point> points = new ArrayList<>();
        int x = p.x - param.range, y = p.y;
        while(x <= p.x + param.range) {
            if(x < 0 || x == p.x) {
                continue;
            }
            if(x >= boardSize) {
                break;
            }
            decideAddPoint(new Point(x, y), points, param);
            x++;
        }
        x = p.x;
        y = p.y - param.range;
        while(y <= p.y + param.range) {
            if(y < 0 || y == p.y) {
                continue;
            }
            if(y >= boardSize) {
                break;
            }
            decideAddPoint(new Point(x, y), points, param);
            y++;
        }
        return points;
    }

    public ArrayList<Point> get8ways(Point p, PointParam param) {
        // the return list DOESN'T contain p
        ArrayList<Point> points = get4Ways(p, param);
        int x = p.x - param.range, y = p.y - param.range, y2 = p.y + param.range;
        while(x <= p.x + param.range) {
            if(x <0 || x == p.x) {
                continue;
            }
            if(x >= boardSize) {
                break;
            }
            if(inBounds(y)) {
                decideAddPoint(new Point(x, y), points, param);
            }
            if(inBounds(y2)) {
                decideAddPoint(new Point(x, y2), points, param);
            }
            x++;
            y++;
            y2--;
        }
        return points;
    }

    public ArrayList<Point> getLine(Point p, int dx, int dy, PointParam param) {
        ArrayList<Point> points = new ArrayList<>();
        int x = p.x + dx, y = p.y + dy;

        for(int i = 0;i < param.range; i++) {
            if(!inBounds(x, y)) {
                break;
            }
            decideAddPoint(new Point(x, y), points, param);
        }
        return points;
    }

    public void summonObject(WarObject object, Point point) throws IllegalArgumentException {
        int x = point.x, y = point.y;
        if(hasObjectOn(x, y)) {
            throw new IllegalArgumentException("There's already an object on ("+x+", "+y+").");
        }
        board[x][y] = object;
        object.updatePosition(point);
    }

    public void transfer(WarObject from, WarObject to) {
        Point p = new Point(from.position.x, from.position.y);
        board[p.x][p.y] = to;
        to.updatePosition(p);
    }

    @Override
    public void teardown() {

        for(GameObject[] line:board) {
            for(GameObject object:line) {
                object.teardown();
            }
        }
        board = null;
    }

    @Override
    public Snapshot takeSnapshot() {
        return new Snapshot();
    }

    public boolean inBase(Player sender, Point p) {
        if(sender.isPlayerA()) {
            return p.x < firstLine;
        } else {
            return p.x >= secondLine;
        }
    }


    public class Snapshot implements eod.snapshots.Snapshot {
        private WarObject[][] allObjects = board;

        public WarObject[][] getAllObjects() {
            return allObjects;
        }
    }
}
