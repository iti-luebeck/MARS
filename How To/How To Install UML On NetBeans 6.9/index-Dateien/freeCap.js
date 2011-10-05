/***************************************************************
*  Copyright notice
*
*  (c) 2007 Stanislas Rolland <stanislas.rolland(arobas)fructifor.ca>
*  All rights reserved
*
*  This script is part of the TYPO3 project. The TYPO3 project is
*  free software; you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation; either version 2 of the License, or
*  (at your option) any later version.
*
*  The GNU General Public License can be found at
*  http://www.gnu.org/copyleft/gpl.html.
*  A copy is found in the textfile GPL.txt and important notices to the license
*  from the author is found in LICENSE.txt distributed with these scripts.
*
*
*  This script is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*
*  This copyright notice MUST APPEAR in all copies of the script!
***************************************************************/
/*
 * Javascript functions for TYPO3 plugin freeCap (sr_freecap)
 *
 * TYPO3 CVS ID: $Id$
 */

/*
 * Loads a new freeCap image
 *
 * @param	string		id: identifier used to uniiquely identify the image
 *
 * @return	void
 */
function newFreeCap(id, noImageMessage) {
	if (document.getElementById) {
			// extract image name from image source (i.e. cut off ?randomness)
		var theImage = document.getElementById("tx_srfreecap_pi2_captcha_image_"+id);
		var parts = theImage.src.split("&amp");
			// add ?(random) to prevent browser/isp caching
			// parts[0] should be id=page_id
			// parts[1] should be L=sys_language_uid
		theImage.src = parts[0] + "&amp;" + parts[1] + "&amp;set=" + Math.round(Math.random()*100000);
	} else {
		alert(noImageMessage ? noImageMessage : "Sorry, we cannot autoreload a new image. Submit the form and a new image will be loaded.");
	}
}

/*
 * Plays the audio captcha
 *
 * @param	string		id: identifier used to uniquely identify the wav file
 * @param	string		wavURL: url of the wave file generating script
 *
 * @return	void
 */
function playCaptcha(id, wavURL, noPlayMessage) {
	if (document.getElementById) {
		var theAudio = document.getElementById("tx_srfreecap_pi2_captcha_playAudio_"+id);
		var wavURLForOpera = wavURL + "&amp;nocache=" + Math.random();
		theAudio.innerHTML = '<object type="audio/x-wav" data="' + wavURLForOpera + '" style="visibility: hidden;" height="0">'
			+ '<param name="type" value="audio/x-wav" />'
			+ '<param name="src" value="' + wavURLForOpera + '" />'
			+ '<param name="filename" value="' + wavURLForOpera + '" />'
			+ '<param name="autoplay" value="true" />'
			+ '<param name="autoStart" value="1" />'
			+ '<param name="hidden" value="true" />'
			+ '<param name="controller" value="false" />'
			+ 'alt : <a href="' + wavURLForOpera + '">' + (noPlayMessage ? noPlayMessage : 'Sorry, we cannot play the word of the image.') + '</a>'
			+ '</object>';
	} else {
		alert(noPlayMessage ? noPlayMessage : "Sorry, we cannot play the word of the image.");
	}
}
