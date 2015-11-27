module.exports = function(grunt) {

	grunt.config.set('less', {
		dist: {
			files: [{
				expand: true,
				cwd: 'src/less/',
				src: ['app.less'],
				dest: 'dist/assets/css/',
				ext: '.css'
			}]
		}
	});

	grunt.loadNpmTasks('grunt-contrib-less');
};
