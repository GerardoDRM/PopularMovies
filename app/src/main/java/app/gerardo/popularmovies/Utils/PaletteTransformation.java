package app.gerardo.popularmovies.Utils;

import android.graphics.Bitmap;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.graphics.Palette;

import com.squareup.picasso.Transformation;

import app.gerardo.popularmovies.R;

/**
 * Created by Gerardo de la Rosa on 28/09/15.
 *
 * I check from  https://www.bignerdranch.com/blog/extracting-colors-to-a-palette-with-android-lollipop/
 * a tutorial about Palette lib, so I try to use it on my project
 *
 */
public class PaletteTransformation implements Transformation{

    int mutedColor = R.attr.colorPrimary;
    private CollapsingToolbarLayout mCollapsingToolbar;

    public PaletteTransformation(CollapsingToolbarLayout collapsingToolbar) {
        this.mCollapsingToolbar = collapsingToolbar;
    }

    /**
     * Because we are using Picasso it is necessary to work
     * with the bitmap in a transformation in order to use
     * Palette lib
     *
     * @param source
     * @return
     */
    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap bitmap = source;

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {
                mutedColor = palette.getMutedColor(R.color.primary);
                mCollapsingToolbar.setContentScrimColor(mutedColor);
                mCollapsingToolbar.setStatusBarScrimColor(R.color.primary_dark);
            }
        });
        return bitmap;
    }

    @Override
    public String key() {
        return "palette-img";
    }
}
