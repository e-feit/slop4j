package dev.feit.slop4j.examples.spring;

import dev.feit.slop4j.SlopAnalyzer;
import dev.feit.slop4j.SlopReport;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SlopAnalysisController {

	private final SlopAnalyzer slopAnalyzer;

	SlopAnalysisController(SlopAnalyzer slopAnalyzer) {
		this.slopAnalyzer = slopAnalyzer;
	}

	@PostMapping("/slop/analyze")
	SlopReport analyze(@RequestBody String text) {
		return slopAnalyzer.analyze(text);
	}
}
