package controlhelper.utils;

import arc.Core;
import arc.math.geom.Geometry;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import controlhelper.core.Vec2Int;
import mindustry.Vars;
import mindustry.core.World;

public class GeometryUtils {
    public static int TileX(float cursorX) {
        Vec2 vec = Core.input.mouseWorld(cursorX, 0);
        if (Vars.control.input.selectedBlock()) {
            vec.sub(Vars.control.input.block.offset, Vars.control.input.block.offset);
        }
        return World.toTile(vec.x);
    }

    public static int TileY(float cursorY) {
        Vec2 vec = Core.input.mouseWorld(0, cursorY);
        if (Vars.control.input.selectedBlock()) {
            vec.sub(Vars.control.input.block.offset, Vars.control.input.block.offset);
        }
        return World.toTile(vec.y);
    }

    public static Vec2Int GetFront(Vec2Int pos, int size, int rotation) {
        int trns = size / 2 + 1;
        return new Vec2Int(pos.x + Geometry.d4(rotation).x * trns, pos.y + Geometry.d4(rotation).y * trns);
    }

    public static Vec2Int GetBack(Vec2Int pos, int size, int rotation) {
        int trns = size / 2 + 1;
        return new Vec2Int(pos.x + Geometry.d4(rotation + 2).x * trns, pos.y + Geometry.d4(rotation + 2).y * trns);
    }

    public static Seq<Vec2Int> GetNeighbours(Vec2Int pos, int size) {
        int trns = size / 2 + 1;
        Seq<Vec2Int> neighbours = new Seq();

        for (int x = pos.x - trns; x <= pos.x + trns; ++x) {
            for (int y = pos.y - trns; y <= pos.y + trns; ++y) {
                if (x >= pos.x + trns || x <= pos.x - trns || y >= pos.y + trns || y <= pos.y - trns) {
                    neighbours.add(new Vec2Int(x, y));
                }
            }
        }

        return neighbours;
    }

    public static Seq<Vec2Int> GetCollisions(Vec2Int pos, int size) {
        int trns = size / 2 + 1;
        Seq<Vec2Int> collisions = new Seq();

        for (int x = pos.x - trns; x <= pos.x + trns; ++x) {
            for (int y = pos.y - trns; y <= pos.y + trns; ++y) {
                if ((x >= pos.x + trns || x <= pos.x - trns || y >= pos.y + trns || y <= pos.y - trns)
                        && (x != pos.x - trns || y != pos.y - trns) && (x != pos.x + trns || y != pos.y - trns)
                        && (x != pos.x - trns || y != pos.y + trns) && (x != pos.x + trns || y != pos.y + trns)) {
                    collisions.add(new Vec2Int(x, y));
                }
            }
        }
        return collisions;
    }

    public static boolean SamePos(Position a, Position b) {
        return a.getX() == b.getX() && a.getY() == b.getY();
    }

    
    public static int GetDirRotation(Vec2Int pos1, Vec2Int pos2) {
        var a = pos1.cpy();
        var b = pos2.cpy();

        Normalize(pos1, pos2);

        var dir = b.sub(a);
        return GetDirRotation(dir);
    }

    public static int GetDirRotation(Vec2Int dir) {
        dir.cpy().abs();
        for (int i = 0; i < Geometry.d4.length; i++) {
            var dir2 = Geometry.d4[i];
            if (dir.x == dir2.x && dir.y == dir2.y)
                return i;
        }
        return -1;
    }

    public static void Normalize(Vec2Int a, Vec2Int b){
        if (a.x > b.x){
            var tmp = a.x;
            a.x = b.x;
            b.x = tmp;
        }
        if (a.y > b.y){
            var tmp = a.y;
            a.y = b.y;
            b.y = tmp;
        }
    }
}
