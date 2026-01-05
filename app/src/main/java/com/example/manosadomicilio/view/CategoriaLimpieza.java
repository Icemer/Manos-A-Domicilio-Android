package com.example.manosadomicilio.view;

import com.example.manosadomicilio.R;

public class CategoriaLimpieza extends BaseCategoriaActivity {
    private static final int ID_LIMPIEZA = 2;

    @Override
    protected int getCategoriaId() {
        return ID_LIMPIEZA;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.categoria_limpieza;
    }

    @Override
    protected String getNoTrabajadoresMessage() {
        return "No se encontraron trabajadores de limpieza";
    }
}
