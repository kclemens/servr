function loadIndexes() {
    d3.csv("index/", function(data) {
        data.splice(0,0,{index:"-- select data file --"})
        var options = d3.select("select").selectAll("option").data(data)
        options.enter()
            .append("option")
            .text(function(d) {return d.index})
        options.exit()
            .remove()
        query()
    })
}


function query() {
    var index = d3.select('select')[0][0].value
    var query = d3.select('input')[0][0].value.toLowerCase()

    if (index == "-- select data file --") {
        console.log("skipping query as no index was selected")
        var div = d3.select("#results").selectAll("div").remove()
        return;
    }

    d3.csv("index/" + index + "/?query=" + query, function (data) {
        // write table header
        headers = []
        for (key in data[0]) {
            headers.push(key)
        }
        var th = d3.select("#results").selectAll("th").data(headers)
        th.text(function (d) {return d})
        th.enter()
          .append("th")
          .text(function (d) {return d})
        th.exit()
          .remove()

        // write table data
        var tr = function(row) {
            var result = ""
            for (key in row) {
                var lowrd = row[key].toLowerCase()
                var indxs = []
                var start = 0
                for (var indx = lowrd.indexOf(query, start); indx > -1; indx = lowrd.indexOf(query, start)) {
                    indxs.push(indx)
                    start = indx + 1
                }
                var val = row[key]
                for (var i = indxs.length-1; i >= 0; i--) {
                    val = val.substring(0, indxs[i]) +
                          "<span style=\"background-color:yellow\">" +
                          val.substring(indxs[i], indxs[i] + query.length) +
                          "</span>" +
                          val.substring(indxs[i] + query.length)
                }
                result += "<td>" + val + "</td>"
            }
            return result
        }
        var div = d3.select("#results").selectAll("tr").data(data)
        div.html(tr)
        div.enter()
           .append("tr")
           .html(tr)
        div.exit()
           .remove()
    })
}