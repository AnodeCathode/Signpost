package gollorum.signpost.minecraft.data;

import gollorum.signpost.Signpost;
import gollorum.signpost.minecraft.block.Post;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

import java.util.Arrays;

public class PostTag extends ItemTagsProvider {

    public static final String Id = "signpost";

    public static final ITag.INamedTag<Item> Tag = ItemTags.makeWrapperTag(Id);

    public PostTag(DataGenerator dataGenerator, Blocks blockTagProvider) {
        super(dataGenerator, blockTagProvider, Signpost.MOD_ID, null);
    }

    @Override
    protected void registerTags() {
        this.getOrCreateBuilder(Tag)
            .add(Arrays.stream(Post.All_INFOS).map(i -> i.post.asItem()).toArray(Item[]::new));
    }

    public static class Blocks extends BlockTagsProvider {

        public static final ITag.INamedTag<Block> Tag = BlockTags.makeWrapperTag(Id);

        public Blocks(DataGenerator generatorIn) {
            super(generatorIn, Signpost.MOD_ID, null);
        }

        @Override
        protected void registerTags() {
            this.getOrCreateBuilder(Tag)
                .add(Arrays.stream(Post.All_INFOS).map(i -> i.post).toArray(Block[]::new));
        }

    }

}