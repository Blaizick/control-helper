package controlhelper.modules.buildingsdepowerer;

import mindustry.content.Blocks;
import arc.struct.Seq;
import mindustry.world.Block;

public class ProducersDepowerer extends BuildingsDepowerer
{
    public ProducersDepowerer() 
    {
        super(new Seq<>(new Block[]
        {
            Blocks.surgeSmelter,
            Blocks.plastaniumCompressor
        }));
    }
}