
var fablabEventSource;
var googleEventSource;

$(document).ready(function() {
	var cal = $('#calendar');
	cal.fullCalendar({
		header: {
			left: 'prev,next today',
			center: 'title',
			right: 'month,agendaWeek,agendaDay '
		},
		lang: CALENDAR.lang,
		firstDay: 1,
		defaultView: 'agendaWeek',
		minTime:"8:00:00",
		maxTime:"22:00:00",
		//defaultView: 'agendaDay',
		editable: true,
		eventSources: [
			{
				url: CALENDAR.ajaxReservation + '/find',
				data: customParam,
			},
			{
				url: 'https://www.google.com/calendar/feeds/nulu08ntleed9c5peukoeaifl8%40group.calendar.google.com/public/basic',
				color: 'green', // an option!
				textColor: 'black' // an option!
			}
		],
		loading: function(isLoading, view) {
			var load = $('#calendarLoading');
			if (isLoading) {
				load.show();
			} else {
				load.hide();
			}
		}
	});
});

function customParam() {
	return {machines: CALENDAR.machinesSelected};
}

function updateCalendar() {
	console.log("updateCalendar");
	$('#calendar').fullCalendar( 'refetchEvents' )
}


