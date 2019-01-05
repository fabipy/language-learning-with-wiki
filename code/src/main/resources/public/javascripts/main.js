$(document).ready(function() {
    $("#content").append("<div id='submit'>Save Statistics</a>");

    var score = 0;

    $("body").on("change", function(event) {
        if (event.target && event.target.nodeName === "SELECT") {
            $(event.target).attr("disabled", "disabled");

            var span = event.target.parentElement;

            if ($(span).hasClass("correct")) {
                $(span).removeClass("correct");
            }

            if ($(span).hasClass("wrong")) {
                $(span).removeClass("wrong");
            }

            $(span).addClass("answer");

            if (event.target.value === span.id) {
                $(span).addClass("correct");
                score += 1;
            } else {
                $(span).addClass("wrong");
            }
        }
    });

    $("#submit").on("click", function() {
        var maxScore = $(".answer").length || 1;
        var date = new Date();
        var wikiUrl = decodeURI(window.location.search.slice(1, -1).split("&")[0].slice(4));

        var stat = new Stat(wikiUrl,
            String(parseFloat(score / maxScore).toFixed(2)),
            date.toLocaleString()
        );

        // Check if there's already a stats array in localStorage
        // if not, make a new one
        var stats = JSON.parse(localStorage.getItem("stats")) || [];
        stats.push(stat);
        localStorage.setItem("stats", JSON.stringify(stats));

        window.location.href = "/stats";
    });

    function Stat(url, score, date) {
        this.url = url;
        this.score = score;
        this.date = date;
    }
});
