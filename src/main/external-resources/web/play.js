var state = {};
var player;
var wait = 0;

function status(k, v, now) {
	state[k] = v;
	if (wait++ > 1000 || now == undefined) { // Update once in 5 seconds for playing status
		console.log('status '+JSON.stringify(arguments));
		$.post('/playerstatus/', JSON.stringify(state));
		wait = 0;
	}
}

function volumeStatus() {
	status('mute', player.muted ? '1' : '0', true, true);
	status('volume', (player.volume * 100).toFixed(0), true);
}

$(document).ready(function() {
	player = document.getElementById('player');

	player.addEventListener('playing', function() {
		status('playback', 'PLAYING', false);
	});
	player.addEventListener('play', function() {
		status('playback', 'PLAYING');
	});
	player.addEventListener('pause', function() {
		status('playback', 'PAUSED');
	});
	player.addEventListener('ended', function() {
		status('playback', 'STOPPED');
	});
	player.addEventListener('error', function() {
		status('playback', 'STOPPED');
	});
	player.addEventListener('timeupdate', function() {
		status('position', player.currentTime.toFixed(0), false);
	});
	player.addEventListener('volumechange', volumeStatus);

	// Send initial status
	volumeStatus();
});

window.onbeforeunload = function() {
	status('playback', 'STOPPED');
}
