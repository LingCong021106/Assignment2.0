package com.example.assignment.databaseEvent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.assignment.user.event.Event

class EventViewModel (
    val database: EventDatabaseDao,
    application: Application) : AndroidViewModel(application){
    private var eventList = MutableLiveData<EventData>()

    fun insert(event: Event){
        database.insert(converter(event))
    }

    fun getAllEvent(): MutableList<EventData>?{
        var events = mutableListOf<EventData>()
        database.getAll()
        return events
    }

    fun clear() {
        database.clear()
    }

    private fun converter(event: Event):EventData{
        var eventData  = EventData()
        eventData.eventId = event.id
        eventData.eventName = event.name
        eventData.eventCategory = event.category
        eventData.eventDescription = event.description
        eventData.eventImage = event.image.toLong()
        eventData.eventRegEndDate = event.registrationEndDate
        eventData.eventOrgName = event.orgatnizationName
        eventData.eventContactNumber = event.contactNumber
        eventData.eventContactPerson = event.contactPerson
        eventData.eventMaxPerson = event.maxPerson
        eventData.eventDate = event.eventDate
        eventData.eventLocation = event.location

        return eventData
    }


}