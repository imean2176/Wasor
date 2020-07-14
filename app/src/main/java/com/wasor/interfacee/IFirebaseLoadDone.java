package com.wasor.interfacee;

import com.wasor.modal.Rac;

import java.util.List;

public interface IFirebaseLoadDone {
    void onFirebaseLoadSuccess(List<Rac> racList);
    void onFirebaseLoadFailed(String message);
}