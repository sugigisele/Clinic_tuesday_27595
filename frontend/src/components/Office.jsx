import axios from 'axios'
import { useEffect, useState } from 'react'
import LibraryButton from './LibraryButton'

const API = 'http://localhost:8082/api/offices'

function Office() {

    const [offices, setOffices] = useState([]);
    const [searchText, setSearchText] = useState("");
    const [roomNumber, setRoomNumber] = useState("");
    const [editingId, setEditingId] = useState(null);

    function getErrorMessage(error) {
        if (error.response && error.response.data && error.response.data.message) {
            return error.response.data.message
        }
        if (error.response && typeof error.response.data === "string") {
            return error.response.data
        }
        return error.message
    }

    async function getOffices() {
        try {
            const response = await axios.get(API)
            setOffices(response.data)
        } catch (error) {
            alert("could not load offices: " + getErrorMessage(error))
        }
    }

    useEffect(() => {
        getOffices()
    }, [])

    function clearForm() {
        setRoomNumber("")
        setEditingId(null)
    }

    async function saveOffice() {
        try {
            await axios.post(API, { roomNumber: roomNumber })
            alert("The office is saved successfully")
            clearForm()
            getOffices()
        } catch (error) {
            alert("office not saved: " + getErrorMessage(error))
        }
    }

    function startUpdate(office) {
        setEditingId(office.id)
        setRoomNumber(office.roomNumber)
    }

    async function updateOffice() {
        try {
            await axios.put(API + '/' + editingId, { roomNumber: roomNumber })
            alert("The office is updated successfully")
            clearForm()
            getOffices()
        } catch (error) {
            alert("office not updated: " + getErrorMessage(error))
        }
    }

    async function deleteOffice(id, room) {

        if (!window.confirm("Delete office " + room + "?")) {
            return
        }
        try {
            await axios.delete(API + '/' + id)
            alert("The office is deleted successfully")
            getOffices()
        } catch (error) {
            alert("office not deleted: " + getErrorMessage(error))
        }
    }

    const text = searchText.toLowerCase()
    const filteredOffices = offices.filter(office =>
        String(office.roomNumber).toLowerCase().includes(text)
    )

    return (
        <div>
            <h1>{editingId === null ? "SAVE OFFICE" : "UPDATE OFFICE"}</h1>
            <label>Office Number:</label>
            <input type="text" value={roomNumber} onChange={e => setRoomNumber(e.target.value)} />

            {editingId === null ? (
                <LibraryButton buttonLabel="SAVE" onClickButton={saveOffice} />
            ) : (
                <div>
                    <LibraryButton buttonLabel="UPDATE" onClickButton={updateOffice} />
                    <LibraryButton buttonLabel="CANCEL" onClickButton={clearForm} />
                </div>
            )}

            <h1>OFFICES</h1>
            <label>Search:</label>
            <input
                type="text"
                placeholder="Office number"
                value={searchText}
                onChange={e => setSearchText(e.target.value)}
            />

            <table border="1">
                <thead>
                    <tr>
                        <th>Office Number</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {filteredOffices.length === 0 ? (
                        <tr>
                            <td colSpan="2">No office found</td>
                        </tr>
                    ) : (
                        filteredOffices.map(office => (
                            <tr key={office.id}>
                                <td>{office.roomNumber}</td>
                                <td>
                                    <LibraryButton buttonLabel="Update" onClickButton={() => startUpdate(office)} />
                                    <LibraryButton buttonLabel="Delete" onClickButton={() => deleteOffice(office.id, office.roomNumber)} />
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
        </div>
    )
}

export default Office

