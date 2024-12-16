package bcc.snakegame;

public class GridPosition {
    public int x;
    public int y;

    public GridPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public GridPosition(GridPosition other) {
        this.x = other.x;
        this.y = other.y;
    }

    public boolean equals(GridPosition other) {
        return this.x == other.x && this.y == other.y;
    }
}
