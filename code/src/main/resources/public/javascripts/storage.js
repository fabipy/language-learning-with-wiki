$(document).ready(function() {
    var stats = JSON.parse(localStorage.getItem("stats"));
    $.each(stats, function(i, stat){
        $("tbody").append(
            "<tr>" +
                "<td><a href='" + decodeURIComponent(stat.url) + "'>" + decodeURIComponent(stat.url) + "</a></td>" +
                "<td>" + stat.score + "</td>" +
                "<td>" + stat.date + "</td>" +
            "</tr>");
    });

    $(".clear-score").on("click", function(event) {
        event.preventDefault();
        localStorage.clear();
        $("tbody tr").remove();
    });
});