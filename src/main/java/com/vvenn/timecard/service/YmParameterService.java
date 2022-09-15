package com.vvenn.timecard.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Service
@Getter
public class YmParameterService {
	private static final DateTimeFormatter FORMAT_PARAM = DateTimeFormatter.ofPattern("uuuuMM");
	private static final DateTimeFormatter FORMAT_DISP = DateTimeFormatter.ofPattern("uuuu 年 MM 月");
	private String ym;
	private int year;
	private int month;

	@Value("${timecard.constants.start-date}")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;

	/**
	 * YmPrameterServiceのフィールドに値をセットします。<br>
	 * ym: 年月、year: 年、month: 月
	 * 
	 * @param ym 年月
	 */
	public void setParam(Optional<String> ym) {
		this.ym = ym.isPresent() ? ym.get() : "";
		YearMonth date = null;
		try {
			date = YearMonth.parse(this.ym, FORMAT_PARAM.withResolverStyle(ResolverStyle.STRICT));
		} catch (DateTimeParseException e) {
			date = YearMonth.now();
		}
		this.ym = date.format(FORMAT_PARAM);
		this.year = date.getYear();
		this.month = date.getMonthValue();
	}

	/**
	 * valueとtextを保持するインナークラス
	 */
	@AllArgsConstructor
	@Getter
	private class SelectOption {
		private String value;
		private String text;
	}

	/**
	 * 年月選択プルダウンのOptionタグで使用する、valueとtextを保持するインスタンスを返却します。
	 * 
	 * @param date 年月
	 * @return SelectOptionインスタンス
	 */
	private SelectOption getOptionData(LocalDate date) {
		return new SelectOption(date.format(FORMAT_PARAM), date.format(FORMAT_DISP));
	}

	/**
	 * 年月選択プルダウンのOptionタグで使用する、LinkedHashMapを返却します。
	 * 
	 * @return 年月選択プルダウン用のLinkedHashMap
	 */
	public LinkedHashMap<String, List<SelectOption>> getSelectOptions() {
		LocalDate currentMonth = LocalDate.now();
		LocalDate baseMonth = LocalDate.of(this.year, this.month, 1);
		LocalDate startMonth = baseMonth.minusYears(1).with(TemporalAdjusters.firstDayOfYear());
		LocalDate endMonth = baseMonth.plusYears(1).with(TemporalAdjusters.lastDayOfYear());
		Stream<LocalDate> period = startMonth.datesUntil(endMonth.plusDays(1), Period.ofMonths(1));
		List<SelectOption> options = period.map(date -> getOptionData(date)).collect(Collectors.toList());
		return new LinkedHashMap<String, List<SelectOption>>() {
			private static final long serialVersionUID = 1L;
			{
				put("表示年月", Arrays.asList(getOptionData(baseMonth)));
				put("前月", Arrays.asList(getOptionData(baseMonth.minusMonths(1))));
				put("次月", Arrays.asList(getOptionData(baseMonth.plusMonths(1))));
				put("今月", Arrays.asList(getOptionData(currentMonth)));
				put("年月指定", options);
			}
		};
	}

	// Csv専用の今月までを表示するプルダウン
	public LinkedHashMap<String, List<SelectOption>> getSelectOptionsForCsv() {
		LocalDate currentMonth = LocalDate.now();
		LocalDate startMonth = this.startDate;
		Stream<LocalDate> period = startMonth.datesUntil(currentMonth, Period.ofMonths(1));
		List<SelectOption> options = period.map(date -> getOptionData(date)).collect(Collectors.toList());
		Collections.reverse(options);
		return new LinkedHashMap<String, List<SelectOption>>() {
			private static final long serialVersionUID = 1L;
			{
				put("今月", Arrays.asList(getOptionData(currentMonth)));
				put("過去分", options);
			}
		};
	}
}