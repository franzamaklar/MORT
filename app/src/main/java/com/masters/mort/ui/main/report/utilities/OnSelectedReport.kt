package com.masters.mort.ui.main.report.utilities

import com.masters.mort.model.deceased_person.DeceasedPerson

interface OnSelectedReport {
    fun onReportSelected(deceasedPerson: DeceasedPerson)
    fun onReportLongSelected(deceasedPerson: DeceasedPerson)
}