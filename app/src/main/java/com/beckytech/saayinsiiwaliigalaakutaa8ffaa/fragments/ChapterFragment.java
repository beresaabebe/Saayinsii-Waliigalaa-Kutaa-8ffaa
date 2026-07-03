package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.fragments;

import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.R;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.activity.BookDetailActivity;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.adapter.PdfRenderAdapter;

public class ChapterFragment extends Fragment {
    private int startPage, endPage;

    public static ChapterFragment newInstance(int start, int end) {
        ChapterFragment fragment = new ChapterFragment();
        Bundle args = new Bundle();
        args.putInt("start", start);
        args.putInt("end", end);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            startPage = getArguments().getInt("start");
            endPage = getArguments().getInt("end");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapter, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        PdfRenderer renderer = ((BookDetailActivity) requireActivity()).getRenderer();
        PdfRenderAdapter adapter = new PdfRenderAdapter(requireActivity(), renderer, startPage, endPage);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
