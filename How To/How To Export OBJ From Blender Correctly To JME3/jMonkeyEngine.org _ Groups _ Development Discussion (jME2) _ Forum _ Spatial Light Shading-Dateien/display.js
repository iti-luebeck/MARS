function jobman_apply_filter() {
	var ii, field;
	var empty = new Array();
	for( ii = 0; ii < jobman_mandatory_ids.length; ii++ ) {
		field = jQuery("[name^=jobman-field-" + jobman_mandatory_ids[ii] + "]");
		
		if( field.length == 1 && '' == field.attr('value') ) {
			empty.push( jobman_mandatory_labels[ii] );
		}
		
		if( field.attr('type') == 'radio' || field.attr('type') == 'checkbox' ) {
			var checked = false;
			
			for( var jj = 0; jj < field.length; jj++ ) {
				if( field[jj].checked ) {
					checked = true;
					break;
				}
			}
			
			if( ! checked ) {
				empty.push( jobman_mandatory_labels[ii] );
			}
		}
	}
	
	if( empty.length > 0 ) {
		var error = jobman_strings['apply_submit_mandatory_warning'] + ":\n";
		for( ii = 0; ii < empty.length; ii++ ) {
			error += empty[ii] + "\n";
		}
		alert( error );
		return false;
	}
	else {
		return true;
	}
}