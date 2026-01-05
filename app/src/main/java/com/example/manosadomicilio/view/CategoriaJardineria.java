package com.example.manosadomicilio.view;

import com.example.manosadomicilio.R;

public class CategoriaJardineria extends BaseCategoriaActivity {
    private static final int ID_JARDINERIA = 5;

    @Override
    protected int getCategoriaId() {
        return ID_JARDINERIA;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.categoria_fumigacion;
    }

    @Override
    protected String getNoTrabajadoresMessage() {
        return "No se encontraron jardineros";
    }
}
