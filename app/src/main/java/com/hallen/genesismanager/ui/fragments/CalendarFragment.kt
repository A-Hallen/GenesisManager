package com.hallen.genesismanager.ui.fragments

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.genesismanager.R
import com.example.genesismanager.databinding.DialogNewEventBinding
import com.example.genesismanager.databinding.FragmentCalendarBinding
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.hallen.genesismanager.Aplication.Companion.prefs
import com.hallen.genesismanager.domain.model.Event
import com.hallen.genesismanager.system.AlarmReceiver
import com.hallen.genesismanager.ui.adapters.EventAdapter
import com.hallen.genesismanager.ui.viewmodels.EventViewModel
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import java.lang.reflect.InvocationTargetException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var adapter: EventAdapter
    private val eventViewModel: EventViewModel by viewModels()
    private lateinit var currentDay: Date
    private val colorTemplates = prefs.getWorkCraft()
    private val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    private val fullTimeFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy-hh:mm-a", Locale.US)
    private val formatter = SimpleDateFormat("hh:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        currentDay = Date()
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        eventViewModel.dayEvents.observe(viewLifecycleOwner) { event ->
            if (event.isEmpty()) {
                binding.eventHeader.visibility = View.INVISIBLE; binding.eventsList.visibility =
                    View.INVISIBLE
            } else {
                if (binding.eventHeader.visibility == View.INVISIBLE) {
                    binding.eventHeader.visibility = View.VISIBLE
                    binding.eventsList.visibility = View.VISIBLE
                }
            }
            adapter.updateEvent(event, requireActivity())
            val calendarEvents = event.map { convertEvent(it) }
            binding.eventsCalendar.removeEvents(calendarEvents)
            binding.eventsCalendar.addEvents(calendarEvents)
            binding.eventsCalendar.invalidate()
        }

        eventViewModel.eventModel.observe(viewLifecycleOwner) { event ->
            val calendarEvents = event.map { convertEvent(it) }
            binding.eventsCalendar.removeAllEvents()
            binding.eventsCalendar.addEvents(calendarEvents)
        }

        binding.newEvent.setOnClickListener {
            newEventDialog()
        }

        eventViewModel.getEvents()
        loadDayEvent(currentDay)
        setCalendarListeners()
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter(
            { event, view ->
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.menuInflater.inflate(R.menu.simple_delete_menu, popupMenu.menu)
                popupMenu.show()
                popupMenu.setOnMenuItemClickListener {
                    deleteEvent(event)
                    true
                }
            },
            { event ->
                newEventDialog(event)
            }
        )

        binding.eventsList.layoutManager = LinearLayoutManager(context)
        binding.eventsList.adapter = adapter
    }

    private fun convertEvent(event1: Event): com.github.sundeepk.compactcalendarview.domain.Event {
        val colorString = colorTemplates.find { it.name == event1.tipo }
        Logger.i("color: ${colorString?.color}")
        val color = Color.parseColor(colorString?.color ?: "#ffffff")
        val time: Date = try {
            dateFormat.parse(event1.time)
        } catch (e: InvocationTargetException) {
            e.printStackTrace(); null
        } catch (e: ParseException) {
            e.printStackTrace(); null
        } ?: Date()
        return com.github.sundeepk.compactcalendarview.domain.Event(color, time.time)
    }

    /**
     *
     *  Sets the listeners for the [CalendarView] and the [ListView]
     */
    private fun setCalendarListeners() {
        // the textview binding.dayName will observe and change its text according to
        val coolFormat = DateFormat.getDateInstance()
        binding.dayName.text = coolFormat.format(Date())
        binding.eventsCalendar.setListener(object :
            CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                currentDay = dateClicked ?: return
                binding.dayName.text = coolFormat.format(dateClicked)
                loadDayEvent(currentDay)
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                binding.dayName.text = coolFormat.format(firstDayOfNewMonth ?: return)
                currentDay = firstDayOfNewMonth
                loadDayEvent(currentDay)
            }
        })

        binding.eventsCalendar.setCurrentDate(Date())
        binding.eventLeft.setOnClickListener { binding.eventsCalendar.scrollLeft() }
        binding.eventRight.setOnClickListener { binding.eventsCalendar.scrollRight() }
        binding.eventsCalendar.shouldDrawIndicatorsBelowSelectedDays(true)
    }

    private fun loadDayEvent(currentDay: Date) {
        val actualDate = dateFormat.format(currentDay)
        eventViewModel.getEvents(actualDate)
    }

    private fun newEventDialog(oldEvent: Event? = null) {
        val context = requireContext()
        val dialog = Dialog(context)
        val tiposDeTrabajo = colorTemplates.map { it.name }

        val dialogBinding = DialogNewEventBinding.inflate(dialog.layoutInflater)

        dialog.apply {
            setContentView(dialogBinding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.apply {
                copyFrom(window!!.attributes)
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                gravity = Gravity.CENTER
            }
            window!!.attributes = layoutParams
        }
        dialogBinding.apply {
            val arrayAdapter = ArrayAdapter(context, R.layout.spinner_item, arrayOf("AM", "PM"))
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            pmAm.adapter = arrayAdapter
            val priorityAdapter = ArrayAdapter(context, R.layout.spinner_item, tiposDeTrabajo)
            priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            eventPriority.adapter = priorityAdapter
            horaEvent.setOnClickListener { showTimePickerDialog(it as TextView, pmAm) }
            cancelButton.setOnClickListener { dialog.dismiss() }

            if (oldEvent != null) {
                pmAm.setSelection(if (oldEvent.hora.substring(6) == "AM") 0 else 1)
                horaEvent.text = oldEvent.hora.dropLast(3)
                dialogEventTitle.setText(oldEvent.title)
                dialogEventDetails.setText(oldEvent.details)
                eventPriority.setSelection(tiposDeTrabajo.indexOf(oldEvent.tipo).coerceAtLeast(0))
                eventNotificar.isChecked = oldEvent.notificar
            }
            okButton.setOnClickListener {
                val timeMode = if (pmAm.selectedItemPosition == 0) "AM" else "PM"
                val title = dialogEventTitle.text.toString()
                val details = dialogEventDetails.text.toString()
                val event = Event(
                    id = oldEvent?.id ?: 0,
                    time = dateFormat.format(currentDay),
                    hora = "${horaEvent.text}-$timeMode",
                    title = title.takeIf { it.isNotBlank() }
                        ?: details.take(20), // If the title is blank, set the title to the first 20 characters of the details
                    details = details,
                    tipo = tiposDeTrabajo[eventPriority.selectedItemPosition],
                    notificar = eventNotificar.isChecked
                )
                if (title.isBlank() && details.isBlank()) {
                    Toast.makeText(context, "El nombre del evento no es válido", Toast.LENGTH_SHORT)
                        .show()
                } else newEvent(event, oldEvent)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun showTimePickerDialog(textView: TextView, pmAm: Spinner) {
        val calendar = Calendar.getInstance()
        val listener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            textView.text = formatter.format(calendar.time)
            if (hourOfDay >= 12) {
                pmAm.setSelection(1)
            } else pmAm.setSelection(0)
        }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog =
            TimePickerDialog(context, R.style.TimePickerTheme, listener, hour, minute, false)
        timePickerDialog.show()
    }

    private fun newEvent(event: Event, oldEvent: Event?) {
        eventViewModel.saveEvent(event)
        if (event.notificar) {
            if (oldEvent != null) borrarRecordatorio(oldEvent)
            crearRecordatorio(event)
        }
    }

    private fun borrarRecordatorio(oldEvent: Event) {
        val timeStamp = fullTimeFormat.parse("${oldEvent.time}-${oldEvent.hora}")?.time ?: return

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java)

        alarmIntent.putExtra("date", timeStamp)
        alarmIntent.putExtra("title", oldEvent.title)
        alarmIntent.putExtra("details", oldEvent.details)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            1,
            alarmIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun deleteEvent(event: Event) {
        eventViewModel.deleteEvent(event.id)
        if (event.notificar) borrarRecordatorio(event)
    }

    private fun crearRecordatorio(event: Event) {
        val time = "${event.time}-${event.hora}"
        val timeStamp = fullTimeFormat.parse(time)?.time ?: return

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java)

        alarmIntent.putExtra("date", timeStamp)
        alarmIntent.putExtra("title", event.title)
        alarmIntent.putExtra("details", event.details)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            1,
            alarmIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeStamp, pendingIntent)

    }
}