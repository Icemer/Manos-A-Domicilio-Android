package com.example.manosadomicilio.view;

import com.example.manosadomicilio.R;

public class CategoriaPintura extends BaseCategoriaActivity {
    private static final int ID_PINTURA = 6;

    @Override
    protected int getCategoriaId() {
        return ID_PINTURA;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.categoria_pintura;
    }

    @Override
    protected String getNoTrabajadoresMessage() {
        return "No se encontraron pintores";
    }
}
