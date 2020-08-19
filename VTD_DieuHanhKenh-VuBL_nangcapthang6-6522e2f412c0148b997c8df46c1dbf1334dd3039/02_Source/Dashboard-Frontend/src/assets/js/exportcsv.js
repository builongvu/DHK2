/**
 * A Highcharts plugin for exporting data from a rendered chart as CSV, XLS or HTML table
 *
 * Author:   Torstein Honsi
 * Licence:  MIT
 * Version:  1.3.5
 */
/*global Highcharts, window, document, Blob */
// import 'highcharts';

(function (Highcharts) {

    'use strict';

    var each = Highcharts.each,
        downloadAttrSupported = document.createElement('a').download !== undefined;

    Highcharts.setOptions({
        lang: {
//            downloadCSV: 'CSV',
            downloadXLS: 'XLS'
        }
    });


    /**
     * Get the data rows as a two dimensional array
     */
    Highcharts.Chart.prototype.getDataRows = function () {
        var options = (this.options.exporting || {}).csv || {},
            xAxis = this.xAxis[0],
            rows = {},
            rowArr = [],
            dataRows,
            names = [],
            i,
            x,
            rowName = xAxis.categories[xAxis.categories.length - 1],
            // Options
            dateFormat = options.dateFormat || '%Y-%m-%d %H:%M:%S',
            namesValue = [],
            vstrtitleNorms =  this.xAxis[this.xAxis.length - 1].categories[0];
        // Loop the series and index values
        i = 0;
        each(this.series, function (series) {
            var keys = series.options.keys,
                pointArrayMap = keys || series.pointArrayMap || ['y'],
                valueCount = pointArrayMap.length,
                j,
                unit = series.tooltipOptions.valueSuffix;

            if (series.options.includeInCSVExport !== false) { // #55
                var seriesOptions = series.options,
                    options = seriesOptions.dataLabels;

                if(namesValue.length === 0 || namesValue[namesValue.length - 1] !== series.name){
                    if(typeof unit !== "undefined" && unit !== null && unit.trim()){
                        names.push(series.name + "<br />(" + unit.trim() + ")");
                    } else {
                        names.push(series.name);
                    }
                    namesValue.push(series.name);
                    each(series.points, function (point) {
                        j = 0;
                        var labelConfig = point.getLabelConfig(),
                            str = options.format ?
                                format(options.format, labelConfig) :
                                options.formatter.call(labelConfig, options);

                        if (!rows[point.x]) {
                            rows[point.x] = [];
                        }
                        rows[point.x].x = point.x;

                        // Pies, funnels etc. use point name in X row
                        if (!series.xAxis) {
                            rows[point.x].name = point.name;
                        }

                        while (j < valueCount) {
                            if (!series.xAxis && typeof str !== "undefined" && str !== null && !isNaN(parseFloat(str))) {
                                rows[point.x][i + j] = parseFloat(str);
                            } else {
                                rows[point.x][i + j] = point[pointArrayMap[j]];
                            }
                            j = j + 1;
                        }
                    });
                    i = i + j;
                } else {
                    each(series.points, function (point) {
                        j = 0;
                        var labelConfig = point.getLabelConfig(),
                            str = options.format ?
                                format(options.format, labelConfig) :
                                options.formatter.call(labelConfig, options);
                        while (j < valueCount) {
                            if (!series.xAxis && typeof str !== "undefined" && str !== null && !isNaN(parseFloat(str))) {
                                rows[point.x][i + j - 1] = parseFloat(str);
                            } else {
                                if (point[pointArrayMap[j]] !== null) {
                                    rows[point.x][i + j - 1] = point[pointArrayMap[j]];
                                }
                            }
                            j = j + 1;
                        }

                    });
                }
            }
        });

        // Make a sortable array
        for (x in rows) {
            if (rows.hasOwnProperty(x)) {
                rowArr.push(rows[x]);
            }
        }
        // Sort it by X values
        rowArr.sort(function (a, b) {
            return a.x - b.x;
        });
        // Add header row
        var categoryName = rowArr[0].name;
        if(!categoryName && rowName.indexOf(" ") !== -1 && !isNaN(parseInt(parseInt(rowName.substr(rowName.indexOf(" ") + 1).trim())))){
            dataRows = [[xAxis.isDatetimeAxis ? 'DateTime' : rowName.substr(0, rowName.indexOf(" "))].concat(names)];
        } else {
            if(typeof vstrtitleNorms !== "undefined" && vstrtitleNorms !== null && vstrtitleNorms.trim() && vstrtitleNorms.trim().indexOf(" ") === -1 && vstrtitleNorms.trim().indexOf("/") === -1) {
                if(vstrtitleNorms.trim().indexOf("\u0338") !== -1) {
                    dataRows = [[xAxis.isDatetimeAxis ? 'DateTime' : vstrtitleNorms.trim().replace("\u0338", "/")].concat(names)];
                } else {
                    dataRows = [[xAxis.isDatetimeAxis ? 'DateTime' : vstrtitleNorms.trim()].concat(names)];
                }
            } else {
                dataRows = [[xAxis.isDatetimeAxis ? 'DateTime' : ""].concat(names)];
            }
        }

        // Transform the rows to CSV
        each(rowArr, function (row) {

            var category = row.name;
            if (!category) {
                if (xAxis.isDatetimeAxis) {
                    category = Highcharts.dateFormat(dateFormat, row.x);
                } else if (xAxis.categories) {
                    category = Highcharts.pick(xAxis.names[row.x], xAxis.categories[row.x], row.x);
                } else {
                    category = row.x;
                }
            }

            // Add the X/date/category
            row.unshift(category);
            dataRows.push(row);
        });

        return dataRows;
    };

    /**
     * Get a CSV string
     */
    Highcharts.Chart.prototype.getCSV = function (useLocalDecimalPoint) {
        var csv = '',
            rows = this.getDataRows(),
            options = (this.options.exporting || {}).csv || {},
            itemDelimiter = options.itemDelimiter || ',', // use ';' for direct import to Excel
            lineDelimiter = options.lineDelimiter || '\n'; // '\n' isn't working with the js csv data extraction

        // Transform the rows to CSV
        each(rows, function (row, i) {
            var val = '',
                j = row.length,
                n = useLocalDecimalPoint ? (1.1).toLocaleString()[1] : '.';
            while (j--) {
                val = row[j];
                if (typeof val === "string") {
                    val = '"' + val + '"';
                }
//                if (typeof val === 'number') {
//                    if (n === ',') {
//                        val = val.toString().replace(".", ",");
//                    }
//                }
                row[j] = val;
            }
            // Add the values
            csv += row.join(itemDelimiter);

            // Add the line delimiter
            if (i < rows.length - 1) {
                csv += lineDelimiter;
            }
        });
        return csv;
    };

    /**
     * Build a HTML table with the data
     */
    Highcharts.Chart.prototype.getTable = function (useLocalDecimalPoint) {
        var html = '<table>',
            rows = this.getDataRows();

        // Transform the rows to HTML
        each(rows, function (row, i) {
            var tag = i ? 'td' : 'th',
                val,
                j,
                n = useLocalDecimalPoint ? (1.1).toLocaleString()[1] : '.';

            if(tag === 'td'){
                html += '<tr>';
            } else {
                html += '<tr class = "headerTable">';
            }
            for (j = 0; j < row.length; j = j + 1) {
                val = (row[j] === null ? "" : row[j]);
                // Add the cell
                if (typeof val === 'number') {
                    if(val.toString().indexOf(".") !== -1){
                        html += '<' + tag + (typeof val === 'number' ? ' class="decimal"' : '') + '>' + val.toString() + '</' + tag + '>';
                    } else {
                        html += '<' + tag + (typeof val === 'number' ? ' class="number"' : '') + '>' + val.toString() + '</' + tag + '>';
                    }
                } else if (j === 0 && tag === 'td') {
                    if (val.indexOf(" ") !== -1) {
                        var lastVal = val.substr(val.indexOf(" ") + 1).trim();
                        if(!isNaN(parseInt(parseInt(lastVal))) && lastVal.indexOf(" ") === -1) {
                            if(lastVal.indexOf("/") !== -1) {
                                html += '<' + tag + ' class="text">' + lastVal + '</' + tag + '>';
                            } else {
                                html += '<' + tag + '>' + lastVal + '</' + tag + '>';
                            }
                        } else {
                            html += '<' + tag + '>' + val + '</' + tag + '>';
                        }
                    } else {
                        if(val.indexOf("/") !== -1) {
                            html += '<' + tag + ' class="text">' + val + '</' + tag + '>';
                        } else {
                            html += '<' + tag + '>' + val + '</' + tag + '>';
                        }
                    }
                } else {
                    html += '<' + tag + '>' + val + '</' + tag + '>';
                }
            }

            html += '</tr>';
        });
        html += '</table>';
        return html;
    };

    function getContent(chart, href, extension, content, MIME) {
        var a,
            blobObject,
            date = new Date(),
            name = (chart.title ? change_alias(chart.title.textStr.toLowerCase()) + '_' + date.yyyymmdd() : 'chart'),
            options = (chart.options.exporting || {}).csv || {},
            url = options.url;

        // Download attribute supported
        if (downloadAttrSupported) {
            a = document.createElement('a');
            a.href = href;
            a.target      = '_blank';
            a.download    = name + '.' + extension;
            if (document.getElementById("WINDOW_NOT_CLOSE") !== null) {
                document.getElementById("WINDOW_NOT_CLOSE").appendChild(a);
            } else if (document.body !== null) {
                document.body.appendChild(a);
            }
            a.click();
            a.remove();

        } else if (window.Blob && window.navigator.msSaveOrOpenBlob) {
            // Falls to msSaveOrOpenBlob if download attribute is not supported
            blobObject = new Blob([content]);
            window.navigator.msSaveOrOpenBlob(blobObject, name + '.' + extension);

        } else {
            // Fall back to server side handling
            Highcharts.post(url, {
                data: content,
                type: MIME,
                extension: extension
            });
        }
    }

    /**
     * Chuyen co dau thanh khong dau
     */
    function change_alias(alias) {
        var str = alias;
        str= str.toLowerCase();
        str = str.replace(/á|à|ả|ạ|ã|ă|ắ|ằ|ẳ|ẵ|ặ|â|ấ|ầ|ẩ|ẫ|ậ/gi, 'a');
        str = str.replace(/é|è|ẻ|ẽ|ẹ|ê|ế|ề|ể|ễ|ệ/gi, 'e');
        str = str.replace(/i|í|ì|ỉ|ĩ|ị/gi, 'i');
        str = str.replace(/ó|ò|ỏ|õ|ọ|ô|ố|ồ|ổ|ỗ|ộ|ơ|ớ|ờ|ở|ỡ|ợ/gi, 'o');
        str = str.replace(/ú|ù|ủ|ũ|ụ|ư|ứ|ừ|ử|ữ|ự/gi, 'u');
        str = str.replace(/ý|ỳ|ỷ|ỹ|ỵ/gi, 'y');
        str = str.replace(/đ/gi, 'd');
        str= str.replace(/!|@|%|\^|\*|\+|\=|\<|\>|\?|\/|,|\.|\:|\;|\'|\"|\&|\#|\[|\]|~|$|_/gi,"-");
        /* tìm và thay thế các kí tự đặc biệt trong chuỗi sang kí tự - */
        str= str.replace(/-+-/gi,"-"); //thay thế 2- thành 1-
        str= str.replace(/^\-+|\-+$/gi,"");
        str = str.replace(/(?:(?:^|\n)\s+|\s+(?:$|\n))/gi,"").replace(/\s+/gi," ");
        //cắt bỏ ký tự - ở đầu và cuối chuỗi
        return str;
    }
    /**
     * Call this on click of 'Download CSV' button
     */
    Highcharts.Chart.prototype.downloadCSV = function () {
        var csv = this.getCSV(true);
        getContent(
            this,
            'data:text/csv,' + csv.replace(/\n/g, '%0A'),
            'csv',
            csv,
            'text/csv'
        );
    };

    /**
     * Call this on click of 'Download XLS' button
     */
    Highcharts.Chart.prototype.downloadXLS = function () {
        var name = (this.title ? this.title.textStr : 'chart'),
            uri = 'data:application/vnd.ms-excel;base64,',
            template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40">' +
                '<head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet>' +
                '<x:Name>Ark1</x:Name>' +
                '<x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]-->' +
                '<style>td{border:.5pt solid windowtext;font-family: Calibri, sans-serif;} .text{mso-number-format:"\@"} .number{mso-number-format:"#,##0"} .decimal{mso-number-format:"#,##0.####"} .headerTable{background: #C4D79B;}</style>' +
                '<meta name=ProgId content=Excel.Sheet>' +
                '<meta charset="UTF-8">' +
                '</head><body>' +
                '<h3  style = "width: 100%; text-align: center; white-space: nowrap;">' + name + '</h3>' +
                this.getTable(true) +
                '</body></html>',
            base64 = function (s) {
                return window.btoa(unescape(encodeURIComponent(s))); // #50
            };
        getContent(
            this,
            uri + base64(template),
            'xls',
            template,
            'application/vnd.ms-excel'
        );
    };


    // Add "Download CSV" to the exporting menu. Use download attribute if supported, else
    // run a simple PHP script that returns a file. The source code for the PHP script can be viewed at
    // https://raw.github.com/highslide-software/highcharts.com/master/studies/csv-export/csv.php
    if (Highcharts.getOptions().exporting) {
        Highcharts.getOptions().exporting.buttons.contextButton.menuItems.push(
//                {
//            textKey: 'downloadCSV',
//            onclick: function () { this.downloadCSV(); }
//        },
        {
            textKey: 'downloadXLS',
            onclick: function () { this.downloadXLS(); }
        });
    }

}(Highcharts));
