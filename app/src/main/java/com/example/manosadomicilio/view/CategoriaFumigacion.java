package com.example.manosadomicilio.view;

import com.example.manosadomicilio.R;

public class CategoriaFumigacion extends BaseCategoriaActivity {
    private static final int ID_FUMIGACION = 4;

    @Override
    protected int getCategoriaId() {
        return ID_FUMIGACION;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.categoria_fumigacion;
    }

    @Override
    protected String getNoTrabajadoresMessage() {
        return "No se encontraron trabajadores de fumigacion";
    }
}
