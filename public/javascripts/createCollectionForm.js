/**
 * Created by Josh on 4/29/16.
 */

function formRepeater(element) {
    $(element).repeater({
        show: function () {
            $(this).slideDown();
        },
        hide: function (remove) {
            if(confirm('Are you sure you want to remove this item?')) {
                $(this).slideUp(remove);
            }
        }
    });
}

$(document).ready(function() {
    formRepeater(".repeat-subjects");
    formRepeater(".repeat-coverages");
    formRepeater(".repeat-spatialCoverages");
    formRepeater(".repeat-hopeTags");
    formRepeater(".repeat-languages");
});

