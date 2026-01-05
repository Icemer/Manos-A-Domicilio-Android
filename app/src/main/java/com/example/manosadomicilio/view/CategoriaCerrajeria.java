package com.example.manosadomicilio.view;

import com.example.manosadomicilio.R;

public class CategoriaCerrajeria extends BaseCategoriaActivity {
    private static final int ID_CERRAJERIA = 3;

    @Override
    protected int getCategoriaId() {
        return ID_CERRAJERIA;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.categoria_cerrajeria;
    }

    @Override
    protected String getNoTrabajadoresMessage() {
        return "No se encontraron cerrajeros";
    }
}
