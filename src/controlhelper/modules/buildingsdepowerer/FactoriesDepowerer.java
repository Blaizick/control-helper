package controlhelper.modules.buildingsdepowerer;

import mindustry.content.Blocks;
import arc.struct.Seq;
import mindustry.world.Block;

public class FactoriesDepowerer extends BuildingsDepowerer
{
    public FactoriesDepowerer() 
    {
        super(new Seq<>(new Block[]
        {
            Blocks.groundFactory,
            Blocks.airFactory,
            Blocks.navalFactory,
            Blocks.additiveReconstructor,
            Blocks.multiplicativeReconstructor,
            Blocks.exponentialReconstructor,
            Blocks.tetrativeReconstructor
        }));
    }
}