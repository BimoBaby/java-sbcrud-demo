const form = document.querySelector("#note-form");
const titleInput = document.querySelector("#title");
const bodyInput = document.querySelector("#body");
const formHeading = document.querySelector("#form-heading");
const formError = document.querySelector("#form-error");
const cancelButton = document.querySelector("#cancel");
const list = document.querySelector("#notes");
const empty = document.querySelector("#empty");

let editingId = null;

form.addEventListener("submit", async (event) => {
	event.preventDefault();
	clearError();
	const payload = {
		title: titleInput.value.trim(),
		body: bodyInput.value
	};
	const response = await fetch(editingId == null ? "/api/notes" : `/api/notes/${editingId}`, {
		method: editingId == null ? "POST" : "PUT",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(payload)
	});
	if (!response.ok) {
		showError(await errorMessage(response));
		return;
	}
	resetForm();
	await loadNotes();
});

cancelButton.addEventListener("click", resetForm);

async function loadNotes() {
	const response = await fetch("/api/notes");
	if (!response.ok) {
		showError("Could not load notes.");
		return;
	}
	const notes = await response.json();
	list.replaceChildren();
	empty.hidden = notes.length > 0;
	for (const note of notes) {
		list.append(renderNote(note));
	}
}

function renderNote(note) {
	const item = document.createElement("li");
	const heading = document.createElement("h3");
	heading.textContent = note.title;
	const body = document.createElement("p");
	body.textContent = note.body || "";
	const actions = document.createElement("div");
	actions.className = "row-actions";

	const edit = document.createElement("button");
	edit.type = "button";
	edit.className = "edit";
	edit.textContent = "Edit";
	edit.addEventListener("click", () => startEdit(note));

	const remove = document.createElement("button");
	remove.type = "button";
	remove.className = "delete";
	remove.textContent = "Delete";
	remove.addEventListener("click", () => removeNote(note.id));

	actions.append(edit, remove);
	item.append(heading, body, actions);
	return item;
}

function startEdit(note) {
	editingId = note.id;
	titleInput.value = note.title;
	bodyInput.value = note.body || "";
	formHeading.textContent = "Edit note";
	cancelButton.hidden = false;
	titleInput.focus();
}

async function removeNote(id) {
	clearError();
	const response = await fetch(`/api/notes/${id}`, { method: "DELETE" });
	if (!response.ok && response.status !== 204) {
		showError(await errorMessage(response));
		return;
	}
	if (editingId === id) {
		resetForm();
	}
	await loadNotes();
}

function resetForm() {
	editingId = null;
	form.reset();
	formHeading.textContent = "New note";
	cancelButton.hidden = true;
	clearError();
}

function showError(message) {
	formError.hidden = false;
	formError.textContent = message;
}

function clearError() {
	formError.hidden = true;
	formError.textContent = "";
}

async function errorMessage(response) {
	try {
		const problem = await response.json();
		if (problem.errors && problem.errors.length > 0) {
			return problem.errors.map((error) => error.defaultMessage || error.message).filter(Boolean).join(" ");
		}
		return problem.detail || problem.message || problem.error || "Request failed.";
	} catch {
		return "Request failed.";
	}
}

loadNotes();
