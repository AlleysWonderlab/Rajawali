package org.rajawali3d.examples.data;

import android.support.annotation.NonNull;

import com.rajawali3d.examples.data.DataSetImpl;

import org.rajawali3d.examples.R;
import org.rajawali3d.examples.examples.materials.ExoTextureFragment;

import java.util.LinkedList;
import java.util.List;

public final class DataSet {

    private static volatile DataSet instance;

    private final List<Category> categories;

    DataSet() {
        categories = createCategories();
        categories.addAll(DataSetImpl.getInstance().getCategories());
    }

    public static synchronized DataSet getInstance() {
        if (instance == null) {
            synchronized (DataSet.class) {
                if (instance == null) {
                    instance = new DataSet();
                }
            }
        }

        return instance;
    }

    @NonNull
    static List<Category> createCategories() {
        List<Category> categories = new LinkedList<>();
        categories.add(new Category(R.string.category_materials, new Example[]{
                new Example(R.string.example_materials_exo_player, ExoTextureFragment.class),
        }));

        return categories;
    }

    public List<Category> getCategories() {
        return categories;
    }

}
