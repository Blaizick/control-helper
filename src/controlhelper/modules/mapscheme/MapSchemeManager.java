/*package controlhelper.modules.mapscheme;

import static controlhelper.ControlHelper.coreDirectory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import arc.Events;
import arc.files.Fi;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.IntMap;
import arc.struct.OrderedSet;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import controlhelper.core.Vec2Int;
import controlhelper.utils.ArrayUtils;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.ctype.ContentType;
import mindustry.game.Schematic;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.game.Schematic.Stile;
import mindustry.io.JsonIO;
import mindustry.io.SaveFileReader;
import mindustry.io.TypeIO;
import mindustry.world.Block;
import mindustry.world.blocks.legacy.LegacyBlock;

public class MapSchemeManager 
{
    public Fi directory;
    public String extension = ".mssm";

    public Fi mapSchemeFile;
    public MapScheme mapScheme;

    public Fi metaFile;
    public MapsMeta meta;

    public final int version = 1;
    public float threasholdDst = 50f;

    public void Init()
    {
        directory = coreDirectory.child("map_schemes/");
        metaFile = directory.child("maps_meta.meta");

        IgnLoadMeta();
        IgnSaveMeta();
        Events.on(WorldLoadEvent.class, e -> 
        {
            GetMapScheme();
        });
    }

    public void IgnSaveMeta()
    {
        try
        {
            SaveMeta();
        }
        catch (Exception e) {}
    }

    public void SaveMeta() throws IOException
    {
        metaFile.writeString("");
        var out = metaFile.write(false, 1024);
        try (DataOutputStream stream = new DataOutputStream(new DeflaterOutputStream(out)))
        {
            stream.writeInt(version);
            stream.write(meta.maps.size);
            for (var mapMeta : meta.maps)
            {
                stream.writeInt(mapMeta.mapHash);
                stream.writeUTF(mapMeta.path);
            }
        }
    }

    public void IgnLoadMeta()
    {
        try
        {
            LoadMeta();
        }
        catch (Exception e) {}
    }
    
    public void LoadMeta() throws IOException
    {
        meta = new MapsMeta();
        if (!metaFile.exists()) return;
        var in = new DataInputStream(metaFile.read(1024));
        try (DataInputStream stream = new DataInputStream(new InflaterInputStream(in)))
        {
            stream.readInt();
            int total = stream.readInt();
            for (int i = 0; i < total; i++)
            {
                var mapHash = stream.readInt();
                var path = stream.readUTF();
                meta.maps.add(new MapMeta(mapHash, path));
            }
        }
    }


    public void IgnWriteMapScheme()
    {
        try
        {
            WriteMapScheme();
        }
        catch (Exception e) 
        {
            Log.err(e);
        }
    }

    public void WriteMapScheme() throws IOException
    {
        mapSchemeFile.writeString("");
        var out = mapSchemeFile.write(false, 1024);
        try (DataOutputStream stream = new DataOutputStream(new DeflaterOutputStream(out)))
        {
            stream.writeInt(version);
            stream.writeInt(mapScheme.mapHash);
            stream.writeInt(mapScheme.schemes.size);
            for (MSScheme scheme : mapScheme.schemes)
            {
                stream.writeFloat(scheme.pos.x);
                stream.writeFloat(scheme.pos.y);

                stream.writeInt(scheme.width);
                stream.writeInt(scheme.height);

                scheme.tags.put("labels", JsonIO.write(scheme.labels.toArray(String.class)));
                stream.writeInt(scheme.tags.size);
                for (var e : scheme.tags.entries())
                {
                    stream.writeUTF(e.key);
                    stream.writeUTF(e.value);
                }

                OrderedSet<Block> blocks = new OrderedSet<>();
                scheme.tiles.each(t -> blocks.add(t.block));

                stream.writeInt(blocks.size);
                for (int j = 0; j < blocks.size; j++)
                {
                    stream.writeUTF(blocks.orderedItems().get(j).name);
                }

                stream.writeInt(scheme.tiles.size);
                for (Stile tile : scheme.tiles)
                {
                    stream.writeInt(blocks.orderedItems().indexOf(tile.block));
                    stream.writeInt(Point2.pack(tile.x, tile.y));
                    TypeIO.writeObject(Writes.get(stream), tile.config);
                    stream.writeByte(tile.rotation);
                }
            }
        }
    }

    public void IgnReadMapScheme()
    {
        try
        {
            ReadMapScheme();
        }
        catch (Exception e) 
        {
            Log.err(e);
        }
    }

    public void ReadMapScheme() throws IOException
    {
        var in = mapSchemeFile.read(1024);
        mapScheme = new MapScheme();
        try (DataInputStream stream = new DataInputStream(new DeflaterInputStream(in)))
        {
            stream.readInt();
            mapScheme.mapHash = stream.readInt();
            var total1 = stream.readInt();
            for (int i = 0; i < total1; i++)
            {
                MSScheme scheme = new MSScheme();
                scheme.pos.x = stream.readFloat();
                scheme.pos.y = stream.readFloat();

                scheme.width = stream.readInt();
                scheme.height = stream.readInt();

                var tags = stream.readInt();
                for (int j = 0; j < tags; j++)
                {
                    scheme.tags.put(stream.readUTF(), stream.readUTF());                    
                }
                
                try
                {
                    scheme.labels.addAll(JsonIO.read(String[].class, scheme.tags.get("labels", "[]")));
                }
                catch (Exception e) {}

                IntMap<Block> blocks = new IntMap<>();
                var total2 = stream.readInt();
                for (int j = 0; j < total2; j++)
                {
                    var name = stream.readUTF();
                    Block block = Vars.content.getByName(ContentType.block, SaveFileReader.fallback.get(name, name));
                    blocks.put(j, block == null || block instanceof LegacyBlock ? Blocks.air : block);
                }

                int total3 = stream.readInt(); 
                for (int j = 0; j < total3; j++)
                {
                    Block block = blocks.get(stream.readInt());
                    int position = stream.readInt();
                    Object config = TypeIO.readObject(Reads.get(stream));
                    byte rotation = stream.readByte();
                    if (block != Blocks.air)
                    {
                        scheme.tiles.add(new Stile(block, Point2.x(position), Point2.y(position), config, rotation));
                    }
                }
            }
        }
    }


    public void GetMapScheme()
    {
        int mapHash = GetMapHash();
        var mapMeta = meta.GetMapMetaWithHash(mapHash);
        if (mapMeta != null)
        {
            if (mapMeta.path == null || mapMeta.path.isEmpty()) return;
            mapSchemeFile = new Fi(mapMeta.path);
            IgnReadMapScheme();
        }
        else
        {
            mapScheme = new MapScheme();
            mapScheme.mapHash = mapHash;
            int counter = 1;
            mapSchemeFile = null;
            while (mapSchemeFile == null || mapSchemeFile.exists())
            {
                mapSchemeFile = directory.child(Vars.state.map.name() + "_" + counter + extension);
                counter++;
            }
            IgnWriteMapScheme();
            meta.maps.add(new MapMeta(mapHash, mapSchemeFile.absolutePath()));
            IgnSaveMeta();
        }
    }


    public int GetMapHash()
    {
        Seq<Integer> floorIds = new Seq<>();
        Vars.world.tiles.eachTile(t -> floorIds.add((int)t.floorID()));
        return floorIds.hashCode();
    }


    public Seq<MSScheme> GetSchemes(Vec2 pos)
    {
        if (pos == null || mapScheme == null || mapScheme.schemes == null) return null;
        Seq<MSScheme> out = new Seq<>();
        for (MSScheme scheme : mapScheme.schemes) 
        {
            if (scheme.pos.dst(pos) <= threasholdDst)
            {
                out.add(scheme);
            }
        }
        return out;
    }

    public void AddScheme(String name, Vec2Int pos1, Vec2Int pos2, Vec2 anchor)
    {
        Schematic schematic = Vars.schematics.create(pos1.x, pos1.y, pos2.x, pos2.y);
        AddScheme(new MSScheme(anchor, schematic));
    }

    public void AddScheme(MSScheme scheme)
    {
        mapScheme.schemes.add(scheme);
        IgnWriteMapScheme();
    }
    
    public void RemoveScheme(MSScheme scheme)
    {
        mapScheme.schemes.remove(scheme);
        IgnWriteMapScheme();
    }

    public void UseScheme(MSScheme scheme)
    {
        ArrayUtils.AddAll(Vars.player.unit().plans, Vars.schematics.toPlans(scheme, (int)scheme.pos.x, (int)scheme.pos.y));
    }



    public class MapsMeta
    {
        public Seq<MapMeta> maps = new Seq<>();
        
        public MapsMeta()
        {
            
        }

        public MapMeta GetMapMetaWithHash(int mapHash)
        {
            for (MapMeta mapMeta : maps) 
            {
                if (mapMeta.mapHash == mapHash) return mapMeta;
            }
            return null;
        }
    }

    public class MapMeta
    {
        public int mapHash;
        public String path;

        public MapMeta() {}
        
        public MapMeta(int mapHash, String path)
        {
            this.mapHash = mapHash;
            this.path = path;
        }

        public MapMeta(int mapHash, Fi file)
        {
            this.mapHash = mapHash;
            this.path = file.absolutePath();
        }
    }

    public class MapScheme
    {
        public Seq<MSScheme> schemes = new Seq<>();
        public int mapHash;
    }

    //скрол вил для выбора схемы
    //обратная совместимость со старыми версиями
    //удобный способ удалять и просматривать схемы
    //возможность отправки схем
    //предпоказ постройки схемы
    //добваление новых схем мануально
    //добавление новых схем автоматически
    //мб сделать как подсказки для кода, но в игре
}*/