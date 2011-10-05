function plug_emp(tf, content){
var contArr; var address;
var txt; var output;
contArr = content.split(";emp:");
address = contArr[5]+'@'+contArr[3]+'.'+contArr[1];
if(tf===false){
document.write(address);
return; }
if(contArr.length > 6) txt = contArr[7]; else txt = address;
document.write('<a href="mailto:' + address + '">' + txt + '</a>');
return;}