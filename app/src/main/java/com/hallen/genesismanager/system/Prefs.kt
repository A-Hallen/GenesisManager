package com.hallen.genesismanager.system

import android.content.Context
import com.hallen.genesismanager.domain.model.WorkCraft
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

class Prefs @Inject constructor(context: Context) {
    private val storage = context.getSharedPreferences("genesisDatabase", 0)

    fun addNewWorkCraft(workCraft: WorkCraft) {
        val jsonData = storage.getString("work_craft", null)
        val jsonArray = if (jsonData == null) {
            JSONArray()
        } else JSONArray(jsonData)

        val jsonObject = JSONObject()
        jsonObject.put("name", workCraft.name)
        jsonObject.put("color", workCraft.color)
        jsonArray.put(jsonObject)
        storage.edit().putString("work_craft", jsonArray.toString()).apply()
    }

    fun getWorkCraft(): MutableSet<WorkCraft> {
        val jsonData = storage.getString("work_craft", null) ?: return mutableSetOf()
        val jsonArray = JSONArray(jsonData)
        val workList = mutableSetOf<WorkCraft>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val name = jsonObject.getString("name")
            val color = jsonObject.getString("color")
            val workCraft = WorkCraft(name = name, color = color)
            workList.add(workCraft)
        }
        return workList
    }


    fun deleteWorkCraft(workCraft: WorkCraft) {
        val listWorks = getWorkCraft().toList()
        val jsonArray = JSONArray()
        for (element in listWorks) {
            val jsonObject = JSONObject()
            if (element.name == workCraft.name) continue
            jsonObject.put("name", element.name)
            jsonObject.put("color", element.color)
            jsonArray.put(jsonObject)
        }
        storage.edit().putString("work_craft", jsonArray.toString()).apply()
    }
}