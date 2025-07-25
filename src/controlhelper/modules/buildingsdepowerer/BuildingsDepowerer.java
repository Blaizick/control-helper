package controlhelper.modules.buildingsdepowerer;

import static controlhelper.ControlHelper.requestExecutor;

import arc.struct.Seq;
import controlhelper.core.requestexecutor.IRequest;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.world.Block;
import mindustry.world.blocks.power.PowerNode;

public class BuildingsDepowerer 
{
    public Seq<Block> depowerBlocks = new Seq<>();
    public Seq<Block> nodeTypes = new Seq<>(new Block[]
    {
        Blocks.powerNode,
        Blocks.powerNodeLarge,
        Blocks.surgeTower
    });

    public BuildingsDepowerer(Seq<Block> depowerBlocks)
    {
        this.depowerBlocks = depowerBlocks;
    }

    public void DepowerBuilds()
    {
        for (Building building : Groups.build)
        {
            if (building == null || building.team != Vars.player.team()) continue;
            if (!depowerBlocks.contains(building.block())) continue;
            
            DepowerBuild(building);
        }
    }

    public void DepowerBuild(Building building)
    {
        for (Building b : building.power.graph.all)
        {
            if (!nodeTypes.contains(b.block) || !(b.block instanceof PowerNode)) continue;
            if (!b.power.links.contains(building.pos())) continue;
            requestExecutor.AddRequest(new IRequest.TileConfig(b, building.pos()));
        }
    }

    public void PowerBuilds()
    {
        for (Building building : Groups.build)
        {
            if (building == null || building.team != Vars.player.team()) continue;
            if (!depowerBlocks.contains(building.block())) continue;
            
            PowerBuild(building);
        }
    }

    public void PowerBuild(Building building)
    {
        PowerNode.getNodeLinks(building.tile, building.block, building.team, other -> 
        {
            if (building.power.links.contains(other.pos())) return;
            if (!nodeTypes.contains(other.block)) return;
            requestExecutor.AddRequest(new IRequest.TileConfig(other, building.pos()));
        });
    }

    public float GetMaxNodeRange()
    {
        float max = 0;
        for (Block block : nodeTypes) 
        {
            if (!(block instanceof PowerNode)) continue;
            PowerNode node = (PowerNode)block;
            if (node.laserRange > max) max = node.laserRange;
        }
        return max * Vars.tilesize;
    }
}
