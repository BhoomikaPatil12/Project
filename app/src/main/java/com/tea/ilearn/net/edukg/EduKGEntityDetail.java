package com.tea.ilearn.net.edukg;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Unique;
import io.objectbox.converter.PropertyConverter;

@Entity
public class EduKGEntityDetail {
    @Id
    public long id;
    @Index @Unique
    String uri;

    @Convert(converter = EduKGRelationsConverter.class, dbType = String.class)
    @SerializedName("content")
    ArrayList<EduKGRelation> relations;
    @Convert(converter = EduKGPropertiesConverter.class, dbType = String.class)
    @SerializedName("property")
    ArrayList<EduKGProperty> properties;
    String label;
    String course;
    String category;
    boolean stared;
    boolean viewed;

    public static class EduKGRelationsConverter implements PropertyConverter<ArrayList<EduKGRelation>, String> {

        @Override
        public ArrayList<EduKGRelation> convertToEntityProperty(String databaseValue) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<EduKGRelation>>(){}.getType();
            ArrayList<EduKGRelation> relations = gson.fromJson(databaseValue, type);
            return relations;
        }

        @Override
        public String convertToDatabaseValue(ArrayList<EduKGRelation> originalObj) {
            Gson gson = new Gson();
            return gson.toJson(originalObj);
        }
    }

    public static class EduKGPropertiesConverter implements PropertyConverter<ArrayList<EduKGProperty>, String> {

        @Override
        public ArrayList<EduKGProperty> convertToEntityProperty(String databaseValue) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<EduKGProperty>>(){}.getType();
            ArrayList<EduKGProperty> properties = gson.fromJson(databaseValue, type);
            return properties;
        }

        @Override
        public String convertToDatabaseValue(ArrayList<EduKGProperty> originalObj) {
            Gson gson = new Gson();
            return gson.toJson(originalObj);
        }
    }

    public String getCourse() {
        return course;
    }

    public String getCategory() {
        return category;
    }

    public String getUri() {
        return uri;
    }

    public ArrayList<EduKGProperty> getProperties() {
        return properties;
    }

    public String getLabel() {
        return label;
    }

    public ArrayList<EduKGRelation> getRelations() {
        return relations;
    }

    public boolean isStared() {
        return stared;
    }

    public boolean isViewed() {
        return viewed;
    }

    public EduKGEntityDetail setCourse(String course) {
        this.course = course;
        return this;
    }

    public EduKGEntityDetail setCategory(String category) {
        this.category = category;
        return this;
    }

    public EduKGEntityDetail setStared(boolean stared) {
        this.stared = stared;
        return this;
    }

    public EduKGEntityDetail setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public EduKGEntityDetail setViewed(boolean viewed) {
        this.viewed = viewed;
        return this;
    }

    public EduKGEntityDetail setLabel(String label) {
        this.label = label;
        return this;
    }
}
