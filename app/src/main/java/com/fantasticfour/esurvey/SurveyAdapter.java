package com.fantasticfour.esurvey;

import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fantasticfour.esurvey.Global.Config;
import com.fantasticfour.esurvey.Objects.Survey;

import java.util.List;

/**
 * Created by earl on 9/21/2016.
 */
public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.SurveyHolder> {
    private List<Survey> surveys;
//    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener{
        void onItemClick(Survey survey);
    }


    public SurveyAdapter(List<Survey> surveys) {
        this.surveys = surveys;
//        this.itemClickListener = itemClickListener;
    }

    @Override
    public SurveyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.survey_row, parent, false);
        return new SurveyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SurveyHolder holder, int position) {
        Survey survey = surveys.get(position);
        holder.title.setText(survey.getSurvey_title());
        holder.pages.setText("Pages: " +survey.getPages().size());
        holder.questions.setText("Questions: " +survey.getQuestionCount());
        int responseCount = survey.getResponses().size();
        int synced = survey.getSyncedResponseCount();
        holder.synced.setText("Synced: " +synced);
        holder.unsynced.setText("Unsynced: " +(responseCount-synced));
//        holder.bind(survey, itemClickListener);
    }
    @Override
    public int getItemCount() {
        return surveys.size();
    }

    public class SurveyHolder extends RecyclerView.ViewHolder{
        public TextView title, synced, unsynced, pages, questions;
        public SurveyHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.survey_title);
            synced = (TextView) itemView.findViewById(R.id.synced);
            unsynced = (TextView) itemView.findViewById(R.id.unsynced);
            pages = (TextView) itemView.findViewById(R.id.pages);
            questions = (TextView) itemView.findViewById(R.id.questions);
        }


        public void bind(final Survey survey, final OnItemClickListener clickListener){
            title.setText(survey.getSurvey_title());
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    clickListener.onItemClick(survey);
//                }
//            });
        }
    }

}
